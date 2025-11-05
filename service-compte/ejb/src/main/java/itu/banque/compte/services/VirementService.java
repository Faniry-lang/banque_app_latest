package itu.banque.compte.services;

import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.remote.VirementServiceRemote;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.daos.LibelleStatutGeneriqueDAO;
import itu.banque.compte.daos.StatutGeneriqueDAO;
import itu.banque.compte.daos.TransactionCourantDao;
import itu.banque.compte.daos.UtilisateurDao;
import itu.banque.compte.daos.VirementDAO;
import itu.banque.compte.entities.CompteCourant;
import itu.banque.compte.entities.LibelleStatutGenerique;
import itu.banque.compte.entities.StatutGenerique;
import itu.banque.compte.entities.TransactionCourant;
import itu.banque.compte.entities.Utilisateur;
import itu.banque.compte.entities.Virement;
import itu.banque.compte.locals.TransactionCourantServiceLocal;
import itu.banque.compte.locals.VirementServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class VirementService implements VirementServiceRemote, VirementServiceLocal {
    @EJB
    StatutGeneriqueDAO statutGeneriqueDAO;

    @EJB
    StatutGeneriqueService statutGeneriqueService;

    @EJB
    LibelleStatutGeneriqueDAO libelleStatutGeneriqueDAO;

    @EJB
    VirementDAO virementDAO;

    @EJB
    TransactionCourantDao transactionCourantDao;

    @EJB
    TransactionCourantServiceLocal transactionCourantService;

    @EJB
    CompteCourantDao compteCourantDao;

    @EJB
    UtilisateurDao utilisateurDao;

    @Override
    public VirementDto valider(VirementDto virementDto, Integer idUtilisateur) throws Exception {
            Virement virement = fromDto(virementDto);

            checkSiVirementExiste(virement);
            if (statutGeneriqueService.siStatutExiste("virement", virement.getId(), "VALIDE")) {
                return toDto(virement);
            }

            Utilisateur utilisateur = this.utilisateurDao.findById(idUtilisateur);

            // checkSiTransactionsInternesExistent(virement);
            // validerTransactionsInternes(virement, utilisateurDto);

            LibelleStatutGenerique libelleValide = getLibelle("virement", "VALIDE");
            creerStatut(virement, libelleValide, utilisateur);

            return toDto(virement);
    }

    @Override
    public VirementDto annuler(VirementDto virementDto, Integer idUtilisateur) throws Exception {
            Virement virement = fromDto(virementDto);

            checkSiVirementExiste(virement);

            if (statutGeneriqueService.siStatutExiste("virement", virement.getId(), "ANNULE")) {
                return toDto(virement);
            }

            Utilisateur utilisateur = this.utilisateurDao.findById(idUtilisateur);

            // checkSiTransactionsInternesExistent(virement);
            // annulerTransactionsInternes(virement, utilisateurDto);

            LibelleStatutGenerique libelleAnnule = getLibelle("virement", "ANNULE");
            creerStatut(virement, libelleAnnule, utilisateur);

            return toDto(virement);
    }

    /* ---------------------- Méthodes privées ---------------------- */

    private void checkSiVirementExiste(Virement virement) {
        if (virement.getId() == null || virement.getId() <= 0) {
            throw new IllegalArgumentException("Le virement n'existe pas en base pour validation ou annulation");
        }
    }

    private void checkSiTransactionsInternesExistent(Virement virement) {
        TransactionCourant entree = virement.getTransactionEntree();
        TransactionCourant sortie = virement.getTransactionSortie();

        boolean entreeExiste = entree != null && entree.getId() != null && entree.getId() > 0;
        boolean sortieExiste = sortie != null && sortie.getId() != null && sortie.getId() > 0;

        if (!entreeExiste || !sortieExiste) {
            throw new IllegalArgumentException("Les transactions internes du virement ne peuvent pas être nulles");
        }
    }

    private void validerTransactionsInternes(Virement virement, UtilisateurDto utilisateurDto) throws Exception {
        transactionCourantService.valider(transactionCourantService.toDto(virement.getTransactionEntree()), utilisateurDto.getId());
        transactionCourantService.valider(transactionCourantService.toDto(virement.getTransactionSortie()), utilisateurDto.getId());
    }

    private void annulerTransactionsInternes(Virement virement, UtilisateurDto utilisateurDto) throws Exception {
        transactionCourantService.annuler(transactionCourantService.toDto(virement.getTransactionEntree()), utilisateurDto.getId());
        transactionCourantService.annuler(transactionCourantService.toDto(virement.getTransactionSortie()), utilisateurDto.getId());
    }

    private LibelleStatutGenerique getLibelle(String tableReference, String libelleStr) {
        LibelleStatutGenerique libelle = libelleStatutGeneriqueDAO.findByLibelleAndTableReference(tableReference, libelleStr);
        if (libelle == null) {
            throw new IllegalArgumentException("Aucun libellé trouvé pour '" + libelleStr + "'");
        }
        return libelle;
    }

    private void creerStatut(Virement virement, LibelleStatutGenerique libelle, Utilisateur utilisateur) {
        StatutGenerique statut = new StatutGenerique(
            null,
            "virement",
            virement.getId(),
            libelle,
            utilisateur,
            virement.getDateVirement()
        );
        statutGeneriqueDAO.create(statut);
    }


    @Override
    public VirementDto toDto(Virement entity) {
        return new VirementDto(
            entity.getId(),
            entity.getCompte().getId(),
            entity.getCompteBeneficiaire().getId(),
            entity.getMontant(),
            entity.getDeviseRef(),
            entity.getDateVirement(),
            entity.getTransactionEntree().getId(),
            entity.getTransactionSortie().getId()
        );
    }

    @Override
    public Virement fromDto(VirementDto dto) {
        Integer id = dto.getId();
        if (id != null && id > 0) {
            Virement entity = virementDAO.findById(id);
            if (entity == null) {
                throw new IllegalArgumentException("Le virement n'existe pas en base pour l'id " + id);
            }
            return entity;
        }

        CompteCourant compteEmetteur = compteCourantDao.findById(dto.getIdCompte());
        CompteCourant compteBeneficiaire = compteCourantDao.findById(dto.getIdCompteBeneficiaire());
        TransactionCourant entree = transactionCourantDao.findById(dto.getIdTransactionEntree());
        TransactionCourant sortie = transactionCourantDao.findById(dto.getIdTransactionSortie());

        return new Virement(id, compteEmetteur, compteBeneficiaire, dto.getMontant(), dto.getDeviseRef(), dto.getDateVirement(), entree, sortie);
    }

    @Override
    public VirementDto findByTransactionId(Integer transactionId) {
        Virement virement = virementDAO.findByTransactionId(transactionId);
        return virement != null ? toDto(virement) : null;
    }

}
