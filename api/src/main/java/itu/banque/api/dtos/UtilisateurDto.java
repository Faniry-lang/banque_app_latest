package itu.banque.api.dtos;

import java.io.Serializable;

public class UtilisateurDto implements Serializable {
    Integer id;
    String nom;
    Integer idDirection;
    Integer roleLvl;
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
    public Integer getIdDirection() {
        return idDirection;
    }
    public void setIdDirection(Integer idDirection) {
        this.idDirection = idDirection;
    }
    public Integer getRoleLvl() {
        return roleLvl;
    }
    public void setRoleLvl(Integer roleLvl) {
        this.roleLvl = roleLvl;
    }
}
