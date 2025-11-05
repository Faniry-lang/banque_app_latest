package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class VirementDto implements Serializable {
    Integer id;
    Integer idCompte;
    Integer idCompteBeneficiaire;
    BigDecimal montant;
    Integer deviseRef;
    LocalDate dateVirement;
    BigDecimal totalFrais;
    public VirementDto(Integer id, Integer idCompte, Integer idCompteBeneficiaire, BigDecimal montant,
            Integer deviseRef, LocalDate dateVirement, BigDecimal totalFrais) {
        this.id = id;
        this.idCompte = idCompte;
        this.idCompteBeneficiaire = idCompteBeneficiaire;
        this.montant = montant;
        this.deviseRef = deviseRef;
        this.dateVirement = dateVirement;
        this.totalFrais = totalFrais;
    }
    public VirementDto() {
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdCompte() {
        return idCompte;
    }
    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }
    public Integer getIdCompteBeneficiaire() {
        return idCompteBeneficiaire;
    }
    public void setIdCompteBeneficiaire(Integer idCompteBeneficiaire) {
        this.idCompteBeneficiaire = idCompteBeneficiaire;
    }
    public BigDecimal getMontant() {
        return montant;
    }
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    public Integer getDeviseRef() {
        return deviseRef;
    }
    public void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }
    public LocalDate getDateVirement() {
        return dateVirement;
    }
    public void setDateVirement(LocalDate dateVirement) {
        this.dateVirement = dateVirement;
    }
    public BigDecimal getTotalFrais() {
        return totalFrais;
    }
    public void setTotalFrais(BigDecimal totalFrais) {
        this.totalFrais = totalFrais;
    }
    
}
