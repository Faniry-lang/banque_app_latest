package itu.banque.compte.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "libelle_statut_generique")
public class LibelleStatutGenerique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "table_reference", nullable = false, length = 50)
    private String tableReference;

    @Column(name = "libelle", nullable = false, length = 30)
    private String libelle;

    protected LibelleStatutGenerique() {}

    public LibelleStatutGenerique(Integer id, String tableReference, String libelle) {
        this.setId(id);
        this.setTableReference(tableReference);
        this.setLibelle(libelle);
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getTableReference() {
        return tableReference;
    }

    private void setTableReference(String tableReference) {
        if (tableReference == null || tableReference.isBlank()) {
            throw new IllegalArgumentException("tableReference ne peut pas être vide");
        }
        this.tableReference = tableReference;
    }

    public String getLibelle() {
        return libelle;
    }

    private void setLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("libelle ne peut pas être vide");
        }
        this.libelle = libelle;
    }
}
