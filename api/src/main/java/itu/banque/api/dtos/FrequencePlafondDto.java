package itu.banque.api.dtos;

import java.io.Serializable;

public class FrequencePlafondDto implements Serializable {
    Integer id;
    String libelle;
    public FrequencePlafondDto(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
    public FrequencePlafondDto() {
    }
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
