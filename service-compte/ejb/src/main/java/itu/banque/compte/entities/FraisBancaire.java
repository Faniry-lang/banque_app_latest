package itu.banque.compte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "frais_bancaire")
public class FraisBancaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "montant_inf")
    BigDecimal montantInf;

    @Column(name = "montant_sup")
    BigDecimal montantSup;

    @Column(name = "frais_forfaitaire")
    BigDecimal fraisForfaitaire;

    @Column(name = "frais_pourcentage")
    BigDecimal fraisPourcentage;

    @Column(name = "date_frais")
    LocalDate dateFrais;

    public FraisBancaire(Integer id, BigDecimal montantInf, BigDecimal montantSup, BigDecimal fraisForfaitaire,
            BigDecimal fraisPourcentage, LocalDate dateFrais) {
        this.setId(id);
        this.setMontantInf(montantInf);
        this.setMontantSup(montantSup);
        this.setFraisForfaitaire(fraisForfaitaire);
        this.setFraisPourcentage(fraisPourcentage);
        this.setDateFrais(dateFrais);

        if(montantInf.compareTo(montantSup) >= 0)
        {
            throw new IllegalArgumentException("Le montant inférieur ne peut pas être supé");
        }

        if(
            (
                fraisForfaitaire == null || 
                fraisForfaitaire.compareTo(BigDecimal.ZERO) <= 0
            ) && 
            (
                fraisPourcentage == null || 
                fraisPourcentage.compareTo(BigDecimal.ZERO) <= 0
            )
        ) {
            throw new IllegalArgumentException("Le frais forfaitaire et le frais pourcentage ne peuvent pas être nuls");
        }
    }

    public FraisBancaire() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontantInf() {
        return montantInf;
    }

    public void setMontantInf(BigDecimal montantInf) {
        this.montantInf = montantInf;
    }

    public BigDecimal getMontantSup() {
        return montantSup;
    }

    public void setMontantSup(BigDecimal montantSup) {
        this.montantSup = montantSup;
    }

    public BigDecimal getFraisForfaitaire() {
        return fraisForfaitaire;
    }

    public void setFraisForfaitaire(BigDecimal fraisForfaitaire) {
        this.fraisForfaitaire = fraisForfaitaire;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    public void setFraisPourcentage(BigDecimal fraisPourcentage) {
        this.fraisPourcentage = fraisPourcentage;
    }

    public LocalDate getDateFrais() {
        return dateFrais;
    }

    public void setDateFrais(LocalDate dateFrais) {
        this.dateFrais = dateFrais;
    }

    
}
