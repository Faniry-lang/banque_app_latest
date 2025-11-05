package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FraisBancaireDto implements Serializable {
    Integer id;
    BigDecimal montantInf;
    BigDecimal montantSup;
    BigDecimal fraisForfaitaire;
    BigDecimal fraisPourcentage;
    LocalDate dateFrais;
    public FraisBancaireDto(Integer id, BigDecimal montantInf, BigDecimal montantSup, BigDecimal fraisForfaitaire,
            BigDecimal fraisPourcentage, LocalDate dateFrais) {
        this.id = id;
        this.montantInf = montantInf;
        this.montantSup = montantSup;
        this.fraisForfaitaire = fraisForfaitaire;
        this.fraisPourcentage = fraisPourcentage;
        this.dateFrais = dateFrais;
    }
    public FraisBancaireDto() {
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
