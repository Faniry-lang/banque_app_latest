package itu.banque.compte.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "validation")
public class Validation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_reference", nullable = false)
    private Integer idReference;

    @Column(name = "table_reference", length = 60)
    private String tableReference;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @Column(name = "date_validation", nullable = false)
    private LocalDateTime dateValidation = LocalDateTime.now();

    public Validation(Integer id, Integer idReference, String tableReference, Utilisateur utilisateur,
            LocalDateTime dateValidation) {
        this.setId(id);
        this.setIdReference(idReference);
        this.setTableReference(tableReference);
        this.setUtilisateur(utilisateur);
        this.setDateValidation(dateValidation);
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdReference() {
        return idReference;
    }

    private void setIdReference(Integer idReference) {
        if(idReference == null)
        {
            throw new IllegalArgumentException("L'id référence pour la validation ne peut pas être nul");
        }
        this.idReference = idReference;
    }

    public String getTableReference() {
        return tableReference;
    }

    private void setTableReference(String tableReference) {
        if(tableReference == null || tableReference.isEmpty())
        {
            throw new IllegalArgumentException("Le nom de la table en référence pour la validation ne peut pas être vide.");
        }
        this.tableReference = tableReference;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    private void setUtilisateur(Utilisateur utilisateur) {
        if(utilisateur == null)
        {
            throw new IllegalArgumentException("L'utilisateur de la validation ne peut pas être nul");
        }
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    private void setDateValidation(LocalDateTime dateValidation) {
        if(dateValidation == null) {
            throw new IllegalArgumentException("La date de validation ne peut pas être nulle");
        }
        if(dateValidation.isAfter(LocalDateTime.now()))
        {
            throw new IllegalArgumentException("La date de validation ne peut pas être supérieure à la date actuelle");
        }
        this.dateValidation = dateValidation;
    }
}
