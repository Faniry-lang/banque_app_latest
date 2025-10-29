package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionCourantDto implements Serializable {
    Integer id;
    Integer idCompte;
    BigDecimal montant;
    Integer idTypeTransaction;
    LocalDate dateTransaction;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdCompte() { return idCompte; }
    public void setIdCompte(Integer idCompte) { this.idCompte = idCompte; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }
    public Integer getIdTypeTransaction() {
        return idTypeTransaction;
    }
    public void setIdTypeTransaction(Integer idTypeTransaction) {
        this.idTypeTransaction = idTypeTransaction;
    }
    
}
