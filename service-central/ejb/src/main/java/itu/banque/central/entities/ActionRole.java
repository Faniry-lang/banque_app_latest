package itu.banque.central.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "action_role")
public class ActionRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom_table", nullable = false, length = 100)
    private String nomTable;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "role_lvl", nullable = false)
    private Integer roleLvl;

    public ActionRole() {}

    public ActionRole(String nomTable, String action, Integer roleLvl) {
        this.nomTable = nomTable;
        this.action = action;
        this.roleLvl = roleLvl;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomTable() { return nomTable; }
    public void setNomTable(String nomTable) { this.nomTable = nomTable; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Integer getRoleLvl() { return roleLvl; }
    public void setRoleLvl(Integer roleLvl) { this.roleLvl = roleLvl; }
}
