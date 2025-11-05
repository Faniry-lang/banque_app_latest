package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreerVirementDto implements Serializable {
    BigDecimal montant;
    Integer idCompte;
    Integer idCompteBeneficiaire;
    LocalDate dateVirement;
    Integer idUtilisateur;
    Integer deviseRef;
    
    public CreerVirementDto(BigDecimal montant, Integer idCompte, Integer idCompteBeneficiaire, LocalDate dateVirement,
            Integer idUtilisateur, Integer deviseRef) {
        this.montant = montant;
        this.idCompte = idCompte;
        this.idCompteBeneficiaire = idCompteBeneficiaire;
        this.dateVirement = dateVirement;
        this.idUtilisateur = idUtilisateur;
        this.deviseRef = deviseRef;
    }

    public CreerVirementDto() {
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
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

    public LocalDate getDateVirement() {
        return dateVirement;
    }

    public void setDateVirement(LocalDate dateVirement) {
        this.dateVirement = dateVirement;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }

    public void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }

    public Integer getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Integer idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
   
}
