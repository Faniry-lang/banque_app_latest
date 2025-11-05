package itu.banque.compte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "virement")
public class Virement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private CompteCourant compte;

    @ManyToOne
    @JoinColumn(name = "id_compte_beneficiaire", nullable = false)
    private CompteCourant compteBeneficiaire;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "devise_ref")
    private Integer deviseRef;

    @Column(name = "date_virement", nullable = false)
    private LocalDate dateVirement = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "id_transaction_entree", nullable = false)
    private TransactionCourant transactionEntree;

    @ManyToOne
    @JoinColumn(name = "id_transaction_sortie", nullable = false)
    private TransactionCourant transactionSortie;

    protected Virement() {}

    public Virement(Integer id, CompteCourant compte, CompteCourant compteBeneficiaire, BigDecimal montant, Integer deviseRef,
            LocalDate dateVirement, TransactionCourant transactionEntree, TransactionCourant transactionSortie) throws IllegalArgumentException {
        this.setId(id);
        this.setCompte(compte);
        this.setCompteBeneficiaire(compteBeneficiaire);
        this.setMontant(montant);
        this.setDeviseRef(deviseRef);
        this.setDateVirement(dateVirement);
        this.setTransactionEntree(transactionEntree);
        this.setTransactionSortie(transactionSortie);

        if(!this.transactionEntree.getCompte().getId().equals(this.getCompteBeneficiaire().getId()))
        {
            throw new IllegalArgumentException("Le compte bénéficiaire enregistré dans la transaction interne est différent de celui du virement");
        }

        if(!this.transactionSortie.getCompte().getId().equals(this.getCompte().getId()))
        {
            throw new IllegalArgumentException("Le compte émetteur enregistré dans la transaction interne est différent de celui du virement");
        }

        if(!this.transactionEntree.getMontant().equals(this.montant) || !this.transactionSortie.getMontant().equals(montant))
        {
            throw new IllegalArgumentException("Les montants des opérations internes sont différentes de celle enregistrée dans le virement");
        }

        if(this.dateVirement.isAfter(LocalDate.now()))
        {
            throw new IllegalArgumentException("La date de virement ne doit pas être supérieure à la date actuelle");
        }

    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public CompteCourant getCompte() {
        return compte;
    }

    private void setCompte(CompteCourant compte) throws IllegalArgumentException {
        if(compte == null) 
        {
            throw new IllegalArgumentException("Le compte émetteur ne peut pas être null");
        }
        this.compte = compte;
    }

    public CompteCourant getCompteBeneficiaire() {
        return compteBeneficiaire;
    }

    private void setCompteBeneficiaire(CompteCourant compteBeneficiaire) throws IllegalArgumentException {
        if(compteBeneficiaire == null) 
        {
            throw new IllegalArgumentException("Le compte bénéficiaire ne peut pas être null");
        }
        this.compteBeneficiaire = compteBeneficiaire;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    private void setMontant(BigDecimal montant) throws IllegalArgumentException {
        if(montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) 
        {
            throw new IllegalArgumentException("Montant du virement invalide");
        }
        this.montant = montant;
    }

    public LocalDate getDateVirement() {
        return dateVirement;
    }

    private void setDateVirement(LocalDate dateVirement) {
        this.dateVirement = dateVirement;
    }

    public TransactionCourant getTransactionEntree() {
        return transactionEntree;
    }

    private void setTransactionEntree(TransactionCourant transactionEntree) {
        this.transactionEntree = transactionEntree;
    }

    public TransactionCourant getTransactionSortie() {
        return transactionSortie;
    }

    private void setTransactionSortie(TransactionCourant transactionSortie) {
        this.transactionSortie = transactionSortie;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }

    public void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }
}
