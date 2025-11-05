package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import itu.banque.compte.entities.CompteCourant;
import itu.banque.compte.entities.Etat;
import itu.banque.compte.entities.FraisBancaire;
import itu.banque.compte.entities.PlafondJournalier;
import itu.banque.compte.entities.TransactionCourant;
import itu.banque.compte.entities.TypeTransaction;
import itu.banque.compte.entities.Utilisateur;
import itu.banque.compte.entities.Validation;
import itu.banque.compte.entities.Virement;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

@Stateless
public class CompteCourantService implements CompteCourantServiceRemote {

    @Inject
    PersistenceObjectManager pom;

    @Override
    public List<CompteCourantDto> getAll() {
        List<CompteCourant> compteCourants = pom.findByCriteria(CompteCourant.class, List.of());
        return compteCourants.stream().map(CompteCourant::toDto).collect(Collectors.toList());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VirementDto virer(VirementDto virementDto) throws NamingException {
        DeviseServiceRemote deviseService = this.getDeviseService();
        Devise devise = deviseService.getByRef(virementDto.getDeviseRef());
        double tauxDeChange = devise != null ? devise.getMontant() : 1;
        BigDecimal montantFinal = virementDto.getMontant().multiply(BigDecimal.valueOf(tauxDeChange));
        BigDecimal totalFrais = BigDecimal.ZERO;

        FraisBancaire fraisBancaire = FraisBancaire.getPourMontant(pom, virementDto.getMontant(), virementDto.getDateVirement());
        if (fraisBancaire != null) {
            totalFrais = fraisBancaire.getTotalFrais(montantFinal);
        }

        Virement virement = Virement.fromDto(pom, virementDto);
        virement.setMontant(montantFinal);
        virement.setTotalFrais(totalFrais);

        CompteCourant compte = virement.getCompte();
        BigDecimal solde = compte.getSolde(pom, virement.getDateVirement());

        if (solde.subtract(virement.getMontant()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Le solde du compte est insuffisant pour faire ce virement");
        }

        List<Virement> virements = compte.getVirementsJournaliers(pom, virement.getDateVirement());
        PlafondJournalier plafondJournalier = PlafondJournalier.getPlafondJournalier(pom, compte, virement.getDateVirement());
        if (plafondJournalier != null && !virements.isEmpty()) {
            BigDecimal sumVirements = virements.stream()
                    .map(Virement::getMontant)
                    .filter(m -> m != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (sumVirements.add(virement.getMontant()).compareTo(plafondJournalier.getMontant()) > 0) {
                throw new IllegalStateException("Ce compte a atteint son plafond de virement journalier");
            }
        }

        Virement savedVirement = pom.save(virement);
        Etat etatVirement = savedVirement.changerEtat(1, null);
        pom.save(etatVirement);

        return savedVirement.toDto();
    }

    @Override
    public CompteCourantDto findById(Integer id) {
        CompteCourant compteCourant = pom.findById(id, CompteCourant.class);
        return compteCourant != null ? compteCourant.toDto() : null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TransactionCourantDto effectuerTransaction(TransactionCourantDto dto) throws NamingException {
        CompteCourant compte = pom.findById(dto.getIdCompte(), CompteCourant.class);
        if (compte == null) {
            throw new IllegalArgumentException("Compte non trouvé pour l'id: " + dto.getIdCompte());
        }

        TypeTransaction typeTransaction = pom.findById(dto.getIdTypeTransaction(), TypeTransaction.class);
        if (typeTransaction == null) {
            throw new IllegalArgumentException("Type de transaction non trouvé pour l'id: " + dto.getIdTypeTransaction());
        }
        
        DeviseServiceRemote deviseService = this.getDeviseService();
        Devise devise = deviseService.getByRef(dto.getDeviseRef());
        double tauxDeChange = devise != null ? devise.getMontant() : 1;
        dto.setMontant(dto.getMontant().multiply(BigDecimal.valueOf(tauxDeChange)));

        if ("DEBIT".equalsIgnoreCase(typeTransaction.getNom())) {
            BigDecimal soldeActuel = compte.getSolde(pom, dto.getDateTransaction());
            if (soldeActuel.subtract(dto.getMontant()).compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("Solde insuffisant pour effectuer cette transaction.");
            }
        }

        TransactionCourant transaction = new TransactionCourant(null, compte, typeTransaction, null, dto.getDeviseRef(), dto.getMontant(), dto.getDateTransaction());
        TransactionCourant savedTransaction = pom.save(transaction);

        return savedTransaction.toDto();
    }

    @Override
    public BigDecimal getSolde(LocalDate date, Integer compteId) {
        CompteCourant compteCourant = pom.findById(compteId, CompteCourant.class);
        if (compteCourant == null) {
            throw new IllegalArgumentException("Aucun compte courant trouvé pour l'id: " + compteId);
        }
        return compteCourant.getSolde(pom, date);
    }

    @Override
    public List<TransactionCourantDto> getAllTransactions(Integer compteId) {
        CompteCourant compteCourant = pom.findById(compteId, CompteCourant.class);
        if (compteCourant == null) {
            throw new IllegalArgumentException("Aucun compte courant trouvé pour l'id: " + compteId);
        }
        return compteCourant.getTransactions().stream().map(TransactionCourant::toDto).collect(Collectors.toList());
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VirementDto validerVirement(Integer virementId, Integer utilisateurId) {
        Virement virement = pom.findById(virementId, Virement.class);
        if (virement == null) {
            throw new IllegalArgumentException("Aucun virement trouvé pour l'id: " + virementId);
        }

        Utilisateur utilisateur = pom.findById(utilisateurId, Utilisateur.class);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé pour l'id: " + utilisateurId);
        }

        Validation validation = virement.valider(utilisateur, null);
        pom.save(validation);

        Criterion debitCriteria = new Criterion("nom", "DEBIT", Operator.EQUALS);
        TypeTransaction debitType = pom.findOneByCriteria(TypeTransaction.class, new ArrayList<>(List.of(debitCriteria)));

        Criterion creditCriteria = new Criterion("nom", "CREDIT", Operator.EQUALS);
        TypeTransaction creditType = pom.findOneByCriteria(TypeTransaction.class, new ArrayList<>(List.of(creditCriteria)));

        if (debitType == null || creditType == null) {
            throw new IllegalStateException("Les types de transaction 'DEBIT' et/ou 'CREDIT' ne sont pas configurés dans la base de données.");
        }

        BigDecimal montantDebit = virement.getMontant();
        TransactionCourant debitTransaction = new TransactionCourant(null, virement.getCompte(), debitType, virement, virement.getDeviseRef(), montantDebit, virement.getDateVirement());
        pom.save(debitTransaction);

        BigDecimal montantCredit = virement.getMontant().subtract(virement.getTotalFrais() != null ? virement.getTotalFrais() : BigDecimal.ZERO);
        TransactionCourant creditTransaction = new TransactionCourant(null, virement.getCompteBeneficiaire(), creditType, virement, virement.getDeviseRef(), montantCredit, virement.getDateVirement());
        pom.save(creditTransaction);

        Etat etatVirementValide = virement.changerEtat(11, null);
        pom.save(etatVirementValide);

        return virement.toDto();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TransactionCourantDto validerTransaction(Integer transactionId, Integer utilisateurId) {
        TransactionCourant transaction = pom.findById(transactionId, TransactionCourant.class);
        if (transaction == null) {
            throw new IllegalArgumentException("Aucune transaction trouvée pour l'id: " + transactionId);
        }

        Utilisateur utilisateur = pom.findById(utilisateurId, Utilisateur.class);
        if (utilisateur == null) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé pour l'id: " + utilisateurId);
        }

        Validation validation = transaction.valider(utilisateur, null);
        pom.save(validation);

        if (transaction.getVirementSource() != null) {
            Virement virementSource = transaction.getVirementSource();

            Etat etatMouvementsValides = virementSource.changerEtat(21, null);
            pom.save(etatMouvementsValides);

            List<Criterion> criteria = new ArrayList<>();
            criteria.add(new Criterion("virementSource.id", virementSource.getId(), Operator.EQUALS));
            criteria.add(new Criterion("id", transaction.getId(), Operator.NOT_EQUALS));
            TransactionCourant autreTransaction = pom.findOneByCriteria(TransactionCourant.class, criteria);

            if (autreTransaction != null) {
                Validation autreValidation = autreTransaction.valider(utilisateur, null);
                pom.save(autreValidation);
            }
        }

        return transaction.toDto();
    }

    public List<VirementDto> getVirementsForCompte(Integer compteId) {
        Criterion compteCriteria = new Criterion("compte.id", compteId, Operator.EQUALS, true);
        Criterion beneficiaireCriteria = new Criterion("compteBeneficiaire.id", compteId, Operator.EQUALS, true);

        List<Virement> virements = pom.findByCriteria(Virement.class, new ArrayList<>(List.of(compteCriteria, beneficiaireCriteria)));
        return virements.stream().map(Virement::toDto).collect(Collectors.toList());
    }

    public List<itu.banque.api.dtos.TypeTransactionDto> getAllTypeTransactions() {
        return pom.findByCriteria(TypeTransaction.class, new ArrayList<>()).stream().map(TypeTransaction::toDto).collect(Collectors.toList());
    }

    public List<Devise> getAllDevises() throws NamingException {
        return getDeviseService().getAll();
    }
}
