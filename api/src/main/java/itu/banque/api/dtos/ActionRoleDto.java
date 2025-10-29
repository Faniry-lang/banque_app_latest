package itu.banque.api.dtos;

import java.io.Serializable;

public class ActionRoleDto implements Serializable {
    Integer id;
    String nomTable;
    String action;
    Integer roleLvl;
    public String getNomTable() {
        return nomTable;
    }
    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Integer getRoleLvl() {
        return roleLvl;
    }
    public void setRoleLvl(Integer roleLvl) {
        this.roleLvl = roleLvl;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
}
