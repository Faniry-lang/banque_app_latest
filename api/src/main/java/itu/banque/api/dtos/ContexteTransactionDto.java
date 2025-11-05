package itu.banque.api.dtos;

import java.io.Serializable;

public class ContexteTransactionDto implements Serializable {
    Integer id;
    String libelle;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
