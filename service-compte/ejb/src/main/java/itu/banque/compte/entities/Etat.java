package itu.banque.compte.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "etat")
public class Etat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_reference", nullable = false)
    private Integer idReference;

    @Column(name = "table_reference", length = 60)
    private String tableReference;

    @Column(name = "etat_num", nullable = false)
    private Integer etatNum;

    @Column(name = "date_etat", nullable = false)
    private LocalDateTime dateEtat = LocalDateTime.now();

    public Etat(Integer id, Integer idReference, String tableReference, Integer etatNum, LocalDateTime dateEtat) {
        this.setId(id);
        this.setIdReference(idReference);
        this.setTableReference(tableReference);
        this.setEtatNum(etatNum);
        this.setDateEtat(dateEtat);
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdReference() {
        return idReference;
    }

    public String getTableReference() {
        return tableReference;
    }

    public Integer getEtatNum() {
        return etatNum;
    }

    public LocalDateTime getDateEtat() {
        return dateEtat;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    private void setIdReference(Integer idReference) {
        if (idReference == null) {
            throw new IllegalArgumentException("L'id référence de l'état ne peut pas être nul.");
        }
        this.idReference = idReference;
    }

    private void setTableReference(String tableReference) {
        if (tableReference == null || tableReference.isEmpty()) {
            throw new IllegalArgumentException("Le nom de la table en référence de l'état ne peut pas être vide ou nul.");
        }
        this.tableReference = tableReference;
    }

    private void setEtatNum(Integer etatNum) {
        if (etatNum == null) {
            throw new IllegalArgumentException("Le numéro d'état (etat_num) ne peut pas être nul.");
        }
        if (etatNum < 0) {
            throw new IllegalArgumentException("Le numéro d'état (etat_num) doit être un entier positif.");
        }
        this.etatNum = etatNum;
    }

    private void setDateEtat(LocalDateTime dateEtat) {
        if (dateEtat == null) {
            throw new IllegalArgumentException("La date de l'état ne peut pas être nulle.");
        }
        if (dateEtat.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'état ne peut pas être supérieure à la date actuelle.");
        }
        this.dateEtat = dateEtat;
    }
}
