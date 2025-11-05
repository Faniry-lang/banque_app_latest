package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.CreerVirementDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.exceptions.VirementInvalideException;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.daos.ContexteTransactionDAO;
import itu.banque.compte.daos.TypeTransactionDao;
import itu.banque.compte.daos.VirementDAO;
import itu.banque.compte.entities.CompteCourant;
import itu.banque.compte.entities.ContexteTransaction;
import itu.banque.compte.entities.TransactionCourant;
import itu.banque.compte.entities.TypeTransaction;
import itu.banque.compte.entities.Virement;
import itu.banque.compte.locals.TransactionCourantServiceLocal;
import itu.banque.compte.locals.VirementServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class CompteCourantService implements CompteCourantServiceRemote {

    @EJB
    private CompteCourantDao compteCourantDao;

    @EJB
    private TransactionCourantServiceLocal transactionCourantService;

    @EJB
    private TypeTransactionDao typeTransactionDao;

    @EJB
    private ContexteTransactionDAO contexteTransactionDao;

    @EJB
    private VirementDAO virementDAO;

    @EJB
    private VirementServiceLocal virementService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VirementDto virer(CreerVirementDto creerVirementDto) throws Exception {
        if (creerVirementDto.getMontant() == null || creerVirementDto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new VirementInvalideException("Le montant du virement ne peut pas être nul ou négatif");
        }
        if (creerVirementDto.getIdCompte() == null || creerVirementDto.getIdCompteBeneficiaire() == null) {
            throw new VirementInvalideException("Les ID de compte émetteur et bénéficiaire sont requis");
        }

        CompteCourant[] comptes = findAndValidateComptes(creerVirementDto.getIdCompte(), creerVirementDto.getIdCompteBeneficiaire());
        CompteCourant compteEmetteur = comptes[0];
        CompteCourant compteBeneficiaire = comptes[1];

        TypeTransaction typeDebit = typeTransactionDao.findByNom("DEBIT");
        TypeTransaction typeCredit = typeTransactionDao.findByNom("CREDIT");
        ContexteTransaction contexte = contexteTransactionDao.findByLibelle("VIREMENT");
        Devise devise = getDeviseService().getByRef(creerVirementDto.getDeviseRef());

        BigDecimal montantFinal = creerVirementDto.getMontant();
        if (devise != null && !"Ariary".equalsIgnoreCase(devise.getNom())) {
            montantFinal = montantFinal.multiply(BigDecimal.valueOf(devise.getMontant()));
        }

        TransactionCourant trSortie = buildTransaction(compteEmetteur, typeDebit, contexte, creerVirementDto, montantFinal);
        TransactionCourant trEntree = buildTransaction(compteBeneficiaire, typeCredit, contexte, creerVirementDto, montantFinal);

        TransactionCourantDto dtoSortieCree = transactionCourantService.effectuerTransaction(transactionCourantService.toDto(trSortie));
        TransactionCourantDto dtoEntreeCree = transactionCourantService.effectuerTransaction(transactionCourantService.toDto(trEntree));

        TransactionCourant sortiePersistante = transactionCourantService.fromDto(dtoSortieCree);
        TransactionCourant entreePersistante = transactionCourantService.fromDto(dtoEntreeCree);

        Virement virement = new Virement(null, compteEmetteur, compteBeneficiaire, montantFinal, creerVirementDto.getDeviseRef(), creerVirementDto.getDateVirement(), entreePersistante, sortiePersistante);
        Virement savedVirement = virementDAO.create(virement);

        return virementService.valider(virementService.toDto(savedVirement), creerVirementDto.getIdUtilisateur());
    }

    @Override
    public List<CompteCourantDto> getAll() {
        return compteCourantDao.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public CompteCourantDto findById(Integer id) {
        CompteCourant compte = compteCourantDao.findById(id);
        return compte != null ? toDto(compte) : null;
    }

    @Override
    public TransactionCourantDto effectuerTransaction(TransactionCourantDto dto) throws Exception {
        return transactionCourantService.effectuerTransaction(dto);
    }

    @Override
    public BigDecimal getSolde(LocalDate date, Integer compteId) {
        return transactionCourantService.getSolde(date, compteId);
    }


    private CompteCourant[] findAndValidateComptes(Integer idEmetteur, Integer idBeneficiaire) throws VirementInvalideException {
        CompteCourant compteEmetteur = compteCourantDao.findById(idEmetteur);
        if (compteEmetteur == null) {
            throw new VirementInvalideException("Aucun compte courant trouvé pour l'émetteur avec l'id: " + idEmetteur);
        }

        CompteCourant compteBeneficiaire = compteCourantDao.findById(idBeneficiaire);
        if (compteBeneficiaire == null) {
            throw new VirementInvalideException("Aucun compte courant trouvé pour le bénéficiaire avec l'id: " + idBeneficiaire);
        }

        return new CompteCourant[]{compteEmetteur, compteBeneficiaire};
    }

    private TransactionCourant buildTransaction(CompteCourant compte, TypeTransaction type, ContexteTransaction contexte, CreerVirementDto dto, BigDecimal montant) {
        TransactionCourant transaction = new TransactionCourant();
        transaction.setCompte(compte);
        transaction.setContexteTransaction(contexte);
        transaction.setTypeTransaction(type);
        transaction.setDeviseRef(dto.getDeviseRef());
        transaction.setMontant(montant);
        transaction.setDateTransaction(dto.getDateVirement());
        return transaction;
    }

    private DeviseServiceRemote getDeviseService() throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8082");
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "applicationAdmin");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "admin123");
        
        final Context context = new InitialContext(jndiProperties);
        String lookupString = "ejb:service-change/service-change-ejb/DeviseService!itu.banque.api.remote.DeviseServiceRemote";
        
        return (DeviseServiceRemote) context.lookup(lookupString);
    }

    public CompteCourantDto toDto(CompteCourant entity) {
        CompteCourantDto dto = new CompteCourantDto();
        dto.setId(entity.getId());
        dto.setIdClient(entity.getClient().getId());
        dto.setDateCreation(entity.getDateCreation());
        dto.setSoldeInitial(entity.getSoldeInitial());
        return dto;
    }

    @Override
    public List<TransactionCourantDto> getAllTransactions(Integer compteId) {
        return this.transactionCourantService.getAll();
    }
}
