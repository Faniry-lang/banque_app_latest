package itu.banque.api.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class Devise implements Serializable {
    private Integer ref;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montant;
    public Devise(Integer ref, String nom, LocalDate dateDebut, LocalDate dateFin, double montant) {
        this.ref = ref;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
    }
    public Devise() {
    }
    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    public double getMontant() {
        return montant;
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }

    @Override
    public String toString() {
        return nom + "," + dateDebut + "," + (dateFin != null ? dateFin : "null") + "," + montant;
    }

}

