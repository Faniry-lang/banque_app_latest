package itu.banque.compte.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "statut_generique")
public class StatutGenerique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "table_reference", nullable = false, length = 50)
    private String tableReference;

    @Column(name = "id_reference", nullable = false)
    private Integer idReference;

    @ManyToOne
    @JoinColumn(name = "id_libelle", nullable = false)
    private LibelleStatutGenerique libelle;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @Column(name = "date_statut", nullable = false)
    private LocalDate dateStatut = LocalDate.now();

    protected StatutGenerique() {}

    public StatutGenerique(Integer id, String tableReference, Integer idReference,
                           LibelleStatutGenerique libelle, Utilisateur utilisateur, LocalDate dateStatut) {

        this.setId(id);
        this.setTableReference(tableReference);
        this.setIdReference(idReference);
        this.setLibelle(libelle);
        this.setUtilisateur(utilisateur);
        this.setDateStatut(dateStatut);

        if (!this.libelle.getTableReference().equals(this.tableReference)) {
            throw new IllegalArgumentException(
                "Le libellé ne correspond pas à la même table_reference que le statut : "
                + this.libelle.getTableReference() + " != " + this.tableReference
            );
        }
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

    public Integer getIdReference() {
        return idReference;
    }

    private void setIdReference(Integer idReference) {
        if (idReference == null || idReference <= 0) {
            throw new IllegalArgumentException("idReference invalide");
        }
        this.idReference = idReference;
    }

    public LibelleStatutGenerique getLibelle() {
        return libelle;
    }

    private void setLibelle(LibelleStatutGenerique libelle) {
        if (libelle == null) {
            throw new IllegalArgumentException("libelle ne peut pas être null");
        }
        this.libelle = libelle;
    }

    public LocalDate getDateStatut() {
        return dateStatut;
    }

    private void setDateStatut(LocalDate dateStatut) {
        if (dateStatut == null) {
            throw new IllegalArgumentException("dateStatut ne peut pas être null");
        }
        this.dateStatut = dateStatut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}
