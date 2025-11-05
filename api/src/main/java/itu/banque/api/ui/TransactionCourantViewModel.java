package itu.banque.api.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionCourantViewModel implements Serializable {
    private Integer id;
    private String typeTransaction;
    private String contexteTransaction;
    private BigDecimal montant;
    private LocalDate dateTransaction;
    private Integer refDevise;
    private String statut;

    public TransactionCourantViewModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public String getContexteTransaction() {
        return contexteTransaction;
    }

    public void setContexteTransaction(String contexteTransaction) {
        this.contexteTransaction = contexteTransaction;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDate dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public Integer getRefDevise() {
        return refDevise;
    }

    public void setRefDevise(Integer refDevise) {
        this.refDevise = refDevise;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
