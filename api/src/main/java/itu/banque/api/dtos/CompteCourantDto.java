package itu.banque.api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompteCourantDto implements Serializable {
    Integer id;
    Integer idClient;
    BigDecimal soldeInitial;
    LocalDate dateCreation;
    List<TransactionCourantDto> transactions;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdClient() {
        return idClient;
    }
    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
    public BigDecimal getSoldeInitial() {
        return soldeInitial;
    }
    public void setSoldeInitial(BigDecimal soldeInitial) {
        this.soldeInitial = soldeInitial;
    }
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    public List<TransactionCourantDto> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<TransactionCourantDto> transactions) {
        this.transactions = transactions;
    }
}
