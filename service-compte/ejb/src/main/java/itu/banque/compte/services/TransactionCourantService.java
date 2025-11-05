package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.exceptions.TransactionInvalideException;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.api.ui.TransactionCourantViewModel;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.daos.ContexteTransactionDAO;
import itu.banque.compte.daos.FrequencePlafondDAO;
import itu.banque.compte.daos.LibelleStatutGeneriqueDAO;
import itu.banque.compte.daos.PlafondDAO;
import itu.banque.compte.daos.StatutGeneriqueDAO;
import itu.banque.compte.daos.TransactionCourantDao;
import itu.banque.compte.daos.TypeTransactionDao;
import itu.banque.compte.daos.UtilisateurDao;
import itu.banque.compte.daos.VirementDAO;
import itu.banque.compte.entities.CompteCourant;
import itu.banque.compte.entities.FrequencePlafond;
import itu.banque.compte.entities.LibelleStatutGenerique;
import itu.banque.compte.entities.Plafond;
import itu.banque.compte.entities.StatutGenerique;
import itu.banque.compte.entities.TransactionCourant;
import itu.banque.compte.entities.TypeTransaction;
import itu.banque.compte.entities.Utilisateur;
import itu.banque.compte.locals.TransactionCourantServiceLocal;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
public class TransactionCourantService implements TransactionCourantServiceRemote, TransactionCourantServiceLocal {

    @EJB
    StatutGeneriqueDAO statutGeneriqueDAO;

    @EJB
    StatutGeneriqueService statutGeneriqueService;

    @EJB
    LibelleStatutGeneriqueDAO libelleStatutGeneriqueDAO;

    @EJB
    TransactionCourantDao transactionCourantDao;

    @EJB
    CompteCourantDao compteCourantDao;

    @EJB
    TypeTransactionDao typeTransactionDao;

    @EJB
    ContexteTransactionDAO contexteTransactionDao;

    @EJB
    PlafondDAO plafondDAO;

    @EJB
    FrequencePlafondDAO frequencePlafondDAO;

    @EJB
    VirementDAO virementDAO;

    @EJB
    UtilisateurDao utilisateurDao;

    @Override
    public List<TransactionCourantDto> getByCompteId(Integer compteId) {
        List<TransactionCourant> transactions = transactionCourantDao.getByCompteId(compteId);
        return transactions.stream()
                           .map(this::toDto)
                           .toList();
    }

    @Override
    public List<TransactionCourantDto> getAll() {
        List<TransactionCourant> transactions = transactionCourantDao.findAll();
        return transactions.stream()
                           .map(this::toDto)
                           .toList();
    }

    @Override
    public TransactionCourantDto create(TransactionCourantDto transactionCourantDto) {
        TransactionCourant transactionCourant = transactionCourantDao.save(fromDto(transactionCourantDto));
        return toDto(transactionCourant);
    }

    @Override
    public void checkDepassementPlafondMensuel(TransactionCourant transactionCourant) throws Exception
    {
        FrequencePlafond frequencePlafond = this.frequencePlafondDAO.findByLibelle("MENSUEL");
        Integer idTypeTransaction = transactionCourant.getTypeTransaction().getId();
        Integer idFrequencePlafond = frequencePlafond.getId();
        Integer idContexteTransaction = transactionCourant.getContexteTransaction().getId(); 
        Integer idCompte = transactionCourant.getCompte().getId();

        Plafond plafondMensuel = this.plafondDAO.findApplicablePlafond(idTypeTransaction, idFrequencePlafond, idContexteTransaction, idCompte, transactionCourant.getDateTransaction());

        if(plafondMensuel == null)
        {
        return;
        }

        BigDecimal cumulParMois = this.getCumulParMois(transactionCourant.getDateTransaction(), idCompte, transactionCourant.getContexteTransaction().getLibelle());

        BigDecimal nouveauCumul = cumulParMois.add(transactionCourant.getMontant());

        if(nouveauCumul.compareTo(plafondMensuel.getMontant()) > 0)
        {
            throw new Exception("Plafond mensuel dépassé pour cette transaction");
        }
    }

    @Override
    public void checkDepassementPlafondOperation(TransactionCourant transactionCourant) throws Exception 
    {
        FrequencePlafond frequencePlafond = this.frequencePlafondDAO.findByLibelle("OPERATION");
        Integer idTypeTransaction = transactionCourant.getTypeTransaction().getId();
        Integer idFrequencePlafond = frequencePlafond.getId();
        Integer idContexteTransaction = transactionCourant.getContexteTransaction().getId(); 
        Integer idCompte = transactionCourant.getCompte().getId();

        Plafond plafondOperation = this.plafondDAO.findApplicablePlafond(idTypeTransaction, idFrequencePlafond, idContexteTransaction, idCompte, transactionCourant.getDateTransaction());

        if(plafondOperation == null)
        {
            return;
        }

        if(transactionCourant.getMontant().compareTo(plafondOperation.getMontant()) > 0)
        {
            throw new Exception("Plafond par opération dépassé pour cette transaction");
        }
    }

    public BigDecimal getCumulParMois(LocalDate date, Integer compteId, String libelle)
    {
        List<TransactionCourant> transactionCourants = this.transactionCourantDao.getAllByMonthAndCompteId(date, compteId, libelle);
        BigDecimal cumul = BigDecimal.ZERO;
        for(TransactionCourant tc : transactionCourants)
        {
            if(tc.getTypeTransaction().getNom().equals("DEBIT"))
            {
                cumul = cumul.subtract(tc.getMontant());
            } else if(tc.getTypeTransaction().getNom().equals("CREDIT"))
            {
                cumul = cumul.add(tc.getMontant());
            }
        }
        return cumul;
    }

    @Override
    public BigDecimal getSolde(LocalDate date, Integer compteId) 
    {
        CompteCourant cc = this.compteCourantDao.findById(compteId);
        if(cc == null)
        {
            throw new IllegalArgumentException("Aucun compte courant trouvé pour l'id "+compteId);
        }

        List<TransactionCourant> allTransactions = this.transactionCourantDao.getAllBeforeDate(date, compteId);
        if (allTransactions.isEmpty()) {
            return cc.getSoldeInitial() != null ? cc.getSoldeInitial() : BigDecimal.ZERO;
        }

        List<Integer> transactionIds = allTransactions.stream().map(TransactionCourant::getId).collect(Collectors.toList());

        List<StatutGenerique> latestStatuses = this.statutGeneriqueDAO.getLastStatutsForIdReferences("transaction_courant", transactionIds);

        java.util.Set<Integer> validTransactionIds = latestStatuses.stream()
            .filter(s -> "VALIDE".equals(s.getLibelle().getLibelle()))
            .map(StatutGenerique::getIdReference)
            .collect(Collectors.toSet());

        List<TransactionCourant> validTransactions = allTransactions.stream()
            .filter(t -> validTransactionIds.contains(t.getId()))
            .collect(Collectors.toList());

        BigDecimal solde = cc.getSoldeInitial() != null ? cc.getSoldeInitial() : BigDecimal.ZERO;
        for (TransactionCourant tc : validTransactions) {
            if ("DEBIT".equals(tc.getTypeTransaction().getNom())) {
                solde = solde.subtract(tc.getMontant());
            } else if ("CREDIT".equals(tc.getTypeTransaction().getNom())) {
                solde = solde.add(tc.getMontant());
            }
        }
        return solde;
    }

    @Override
    public TransactionCourant fromDto(TransactionCourantDto dto) {

        if(dto.getId() != null && dto.getId() > 0)
        {
            TransactionCourant entity = this.transactionCourantDao.findById(dto.getId());
            if(entity == null) {
               throw new IllegalArgumentException("La transaction n'existe pas en base pour l'id "+dto.getId());
            }
            return entity;
        }

        TransactionCourant entity = new TransactionCourant();
        entity.setId(dto.getId());
        entity.setCompte(compteCourantDao.findById(dto.getIdCompte()));
        entity.setTypeTransaction(typeTransactionDao.findById(dto.getIdTypeTransaction()));
        entity.setContexteTransaction(contexteTransactionDao.findById(dto.getIdContexteTransaction()));
        entity.setVirementSource(virementDAO.findById(dto.getIdVirementSource()));
        entity.setDeviseRef(dto.getDeviseRef());
        entity.setMontant(dto.getMontant());
        entity.setDateTransaction(dto.getDateTransaction());
        return entity;
    }

    @Override
    public TransactionCourantDto toDto(TransactionCourant entity) {
        TransactionCourantDto dto = new TransactionCourantDto();
        dto.setId(entity.getId());
        dto.setIdCompte(entity.getCompte().getId());
        dto.setIdTypeTransaction(entity.getTypeTransaction().getId());
        dto.setIdContexteTransaction(entity.getContexteTransaction().getId());
        dto.setIdVirementSource(entity.getVirementSource().getId());
        dto.setDeviseRef(entity.getDeviseRef());
        dto.setMontant(entity.getMontant());
        dto.setDateTransaction(entity.getDateTransaction());
        return dto;
    }

    @Override
    public TransactionCourantDto valider(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws Exception {
            TransactionCourant transactionCourant = fromDto(transactionCourantDto);
            if(transactionCourant.getId() == null || transactionCourant.getId() <= 0)
            {
                throw new TransactionInvalideException("La transaction à valider n'existe pas dans la base de donnée");
            }

            if(this.statutGeneriqueService.siStatutExiste("transaction_courant", transactionCourant.getId(), "VALIDE"))
            {
                return toDto(transactionCourant);
            }

    
            Utilisateur utilisateurEntity = idUtilisateur != null ? this.utilisateurDao.findById(idUtilisateur) : null;

            LibelleStatutGenerique libelleStatutGenerique = this.libelleStatutGeneriqueDAO.findByLibelleAndTableReference("transaction_courant", "VALIDE");
            StatutGenerique statutGenerique = new StatutGenerique(null, "transaction_courant", transactionCourant.getId(), libelleStatutGenerique, utilisateurEntity, transactionCourant.getDateTransaction());
    
            this.statutGeneriqueDAO.create(statutGenerique);
            return toDto(transactionCourant);
    }

    @Override
    public TransactionCourantDto annuler(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws TransactionInvalideException {
            TransactionCourant transactionCourant = fromDto(transactionCourantDto);

            if (transactionCourant.getId() == null || transactionCourant.getId() <= 0) {
                throw new TransactionInvalideException("La transaction à annuler n'existe pas dans la base de donnée");
            }

            if (this.statutGeneriqueService.siStatutExiste("transaction_courant", transactionCourant.getId(), "ANNULE")) {
                return toDto(transactionCourant);
            }

            LibelleStatutGenerique libelleStatutGenerique = 
                this.libelleStatutGeneriqueDAO.findByLibelleAndTableReference("transaction_courant", "ANNULE");

            if (libelleStatutGenerique == null) {
                throw new IllegalArgumentException("Aucun libellé trouvé pour 'ANNULE'");
            }

            Utilisateur utilisateurEntity = idUtilisateur != null ? this.utilisateurDao.findById(idUtilisateur) : null;

            StatutGenerique statutGenerique = new StatutGenerique(
                null,
                "transaction_courant",
                transactionCourant.getId(),
                libelleStatutGenerique,
                utilisateurEntity,
                transactionCourant.getDateTransaction()
            );

            this.statutGeneriqueDAO.create(statutGenerique);
            return toDto(transactionCourant);
    }

    public TransactionCourantDto mettreEnAttente(TransactionCourantDto transactionCourantDto, UtilisateurDto utilisateur) throws TransactionInvalideException
    {
        TransactionCourant transactionCourant = fromDto(transactionCourantDto);

        if (transactionCourant.getId() == null || transactionCourant.getId() <= 0) {
            throw new TransactionInvalideException("La transaction à mettre en attente n'existe pas dans la base de donnée");
        }

        if (this.statutGeneriqueService.siStatutExiste("transaction_courant", transactionCourant.getId(), "EN_ATTENTE")) {
            return toDto(transactionCourant);
        }

        LibelleStatutGenerique libelleStatutGenerique = 
            this.libelleStatutGeneriqueDAO.findByLibelleAndTableReference("transaction_courant", "EN_ATTENTE");

        if (libelleStatutGenerique == null) {
            throw new IllegalArgumentException("Aucun libellé trouvé pour 'EN_ATTENTE'");
        }

        Utilisateur utilisateurEntity = (utilisateur != null && utilisateur.getId() != null) ? this.utilisateurDao.findById(utilisateur.getId()) : null;

        StatutGenerique statutGenerique = new StatutGenerique(
            null,
            "transaction_courant",
            transactionCourant.getId(),
            libelleStatutGenerique,
            utilisateurEntity,
            transactionCourant.getDateTransaction()
        );

        this.statutGeneriqueDAO.create(statutGenerique);
        return toDto(transactionCourant);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TransactionCourantDto effectuerTransaction(TransactionCourantDto transactionCourantDto) throws Exception
    {
        TransactionCourant transaction = this.fromDto(transactionCourantDto);
        
        this.checkDepassementPlafondMensuel(transaction);
        this.checkDepassementPlafondOperation(transaction);

        BigDecimal solde = this.getSolde(transaction.getDateTransaction(), transaction.getCompte().getId());

        if(transaction.getTypeTransaction().getNom().equalsIgnoreCase("DEBIT") && solde.subtract(transaction.getMontant()).compareTo(BigDecimal.ZERO) < 0)
        {
            throw new IllegalArgumentException("Opération non permise: Le solde ne peut pas être négatif");
        }
        
        TransactionCourantDto transactionSauve = this.create(transactionCourantDto);
        TransactionCourantDto transactionEnAttente = this.mettreEnAttente(transactionSauve, null);

        return transactionEnAttente;
    }

    @Override 
    public List<TransactionCourantDto> getAllByCompteId(Integer compteId)
    {
        List<TransactionCourant> tc = this.transactionCourantDao.getByCompteId(compteId);
        return tc.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TransactionCourantViewModel> getTransactionViewModelsByCompteId(Integer compteId) {
        List<TransactionCourant> transactions = this.transactionCourantDao.getByCompteId(compteId);
        return this.getViewModelFromTransactionCourants(transactions);
    }

    public List<TransactionCourantViewModel> getViewModelFromTransactionCourants(List<TransactionCourant> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Integer> ids = transactions.stream().map(TransactionCourant::getId).collect(Collectors.toList());
        List<StatutGenerique> derniersStatuts = statutGeneriqueDAO.getLastStatutsForIdReferences("transaction_courant", ids);
        java.util.Map<Integer, String> mapStatuts = derniersStatuts.stream()
                .collect(Collectors.toMap(StatutGenerique::getIdReference, s -> s.getLibelle().getLibelle()));

        List<TransactionCourantViewModel> viewModels = new java.util.ArrayList<>();
        for (TransactionCourant tx : transactions) {
            TransactionCourantViewModel vm = new TransactionCourantViewModel();
            vm.setId(tx.getId());
            vm.setTypeTransaction(tx.getTypeTransaction().getNom());
            vm.setContexteTransaction(tx.getContexteTransaction().getLibelle());
            vm.setMontant(tx.getMontant());
            vm.setDateTransaction(tx.getDateTransaction());
            vm.setRefDevise(tx.getDeviseRef());
            vm.setStatut(mapStatuts.get(tx.getId()));

            viewModels.add(vm);
        }

        return viewModels;
    }

    @Override
    public TransactionCourantDto findById(Integer id) {
        TransactionCourant tc = this.transactionCourantDao.findById(id);
        if(tc == null)
        {
            return null;
        }
        return toDto(tc);
    }

    @Override
    public List<TransactionCourantDto> getByDeviseRef(Integer deviseRef) {
        List<TransactionCourant> transactions = transactionCourantDao.getByDeviseRef(deviseRef);
        return transactions.stream()
                           .map(this::toDto)
                           .toList();
    }

    @Override
    public List<TransactionCourantDto> update(List<TransactionCourantDto> dtos)
    {
        List<TransactionCourant> transactionCourants = dtos.stream().map(this::directMappingFromDto).collect(Collectors.toList());
        List<TransactionCourant> updatedTransactionCourants = this.transactionCourantDao.update(transactionCourants);
        return updatedTransactionCourants.stream().map(this::toDto).collect(Collectors.toList());
    }

    private TransactionCourant directMappingFromDto(TransactionCourantDto dto)
    {
        TransactionCourant entity = new TransactionCourant();
        entity.setId(dto.getId());
        entity.setCompte(compteCourantDao.findById(dto.getIdCompte()));
        entity.setTypeTransaction(typeTransactionDao.findById(dto.getIdTypeTransaction()));
        entity.setContexteTransaction(contexteTransactionDao.findById(dto.getIdContexteTransaction()));
        entity.setVirementSource(virementDAO.findById(dto.getIdVirementSource()));
        entity.setDeviseRef(dto.getDeviseRef());
        entity.setMontant(dto.getMontant());
        entity.setDateTransaction(dto.getDateTransaction());
        return entity;
    }
}
