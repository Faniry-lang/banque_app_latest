package itu.banque.api.dtos;

import java.io.Serializable;

public class TypeTransactionDto implements Serializable {
    Integer id;
    String nom;
    String description;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
