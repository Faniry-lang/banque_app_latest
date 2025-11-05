package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PlafondDto implements Serializable {
    Integer id;
    BigDecimal montant;
    Integer idTypeTransaction;
    Integer idFrequencePlafond;
    Integer idCompte;
    Integer idContexteTransaction;
    LocalDate dateDebut;
    LocalDate dateFin;
    public PlafondDto(Integer id, BigDecimal montant, Integer idTypeTransaction, Integer idFrequencePlafond,
            Integer idContexteTransaction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idFrequencePlafond = idFrequencePlafond;
        this.idContexteTransaction = idContexteTransaction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    public PlafondDto() {
    }
    public PlafondDto(Integer id, BigDecimal montant, Integer idTypeTransaction, Integer idFrequencePlafond,
            Integer idCompte, Integer idContexteTransaction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.montant = montant;
        this.idTypeTransaction = idTypeTransaction;
        this.idFrequencePlafond = idFrequencePlafond;
        this.idCompte = idCompte;
        this.idContexteTransaction = idContexteTransaction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public BigDecimal getMontant() {
        return montant;
    }
    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
    public Integer getIdTypeTransaction() {
        return idTypeTransaction;
    }
    public void setIdTypeTransaction(Integer idTypeTransaction) {
        this.idTypeTransaction = idTypeTransaction;
    }
    public Integer getIdFrequencePlafond() {
        return idFrequencePlafond;
    }
    public void setIdFrequencePlafond(Integer idFrequencePlafond) {
        this.idFrequencePlafond = idFrequencePlafond;
    }
    public Integer getIdCompte() {
        return idCompte;
    }
    public void setIdCompte(Integer idCompte) {
        this.idCompte = idCompte;
    }
    public Integer getIdContexteTransaction() {
        return idContexteTransaction;
    }
    public void setIdContexteTransaction(Integer idContexteTransaction) {
        this.idContexteTransaction = idContexteTransaction;
    }
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
