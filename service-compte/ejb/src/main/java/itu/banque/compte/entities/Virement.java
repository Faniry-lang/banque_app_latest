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

    public Virement(Integer id, CompteCourant compte, CompteCourant compteBeneficiaire, BigDecimal montant,
            Integer deviseRef, LocalDate dateVirement) {
        this.setId(id);
        this.setCompte(compte);
        this.setCompteBeneficiaire(compteBeneficiaire);
        this.setMontant(montant);
        this.setDeviseRef(deviseRef);
        this.setDateVirement(dateVirement);
    }

    protected Virement() {}

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public CompteCourant getCompte() {
        return compte;
    }

    private void setCompte(CompteCourant compte) {
        if(compte == null)
        {
            throw new IllegalArgumentException("Le compte émetteur du virement ne peut pas être nul");
        }
        this.compte = compte;
    }

    public CompteCourant getCompteBeneficiaire() {
        return compteBeneficiaire;
    }

    private void setCompteBeneficiaire(CompteCourant compteBeneficiaire) {
        if(compteBeneficiaire == null)
        {
            throw new IllegalArgumentException("Le compte bénéficiaire du virement ne peut pas être nul");
        }
        this.compteBeneficiaire = compteBeneficiaire;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    private void setMontant(BigDecimal montant) {
        if(montant == null)
        {
            throw new IllegalArgumentException("Le montant ne peut pas être nul");
        }
        if(montant.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Le montant du virement ne peut pas être inférieur ou égal à 0");
        }
        this.montant = montant;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }

    private void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }

    public LocalDate getDateVirement() {
        return dateVirement;
    }

    private void setDateVirement(LocalDate dateVirement) {
        if(dateVirement.isAfter(LocalDate.now()))
        {
            throw new IllegalArgumentException("La date du virement ne peut pas être inférieur à la date actuelle");
        }
        this.dateVirement = dateVirement;
    }
}
