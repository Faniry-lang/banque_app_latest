package itu.banque.compte.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "frequence_plafond")
public class FrequencePlafond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "libelle", nullable =false)
    String libelle;

    public FrequencePlafond() {}

    public FrequencePlafond(Integer id, String libelle) {
        this.setId(id);
        this.setLibelle(libelle);
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    private void setLibelle(String libelle) {
        this.libelle = libelle;
    };
}
