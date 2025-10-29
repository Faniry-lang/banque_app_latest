package itu.banque.api.dtos;

import java.io.Serializable;

public class DirectionDto implements Serializable {
    Integer id;
    String nom;
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
}
