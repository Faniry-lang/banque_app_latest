package itu.banque.api.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class Devise implements Serializable {
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montant;

    public Devise(String nom, LocalDate dateDebut, LocalDate dateFin, double montant) {
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
    }

    public String getNom() { return nom; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public double getMontant() { return montant; }

    @Override
    public String toString() {
        return nom + "," + dateDebut + "," + (dateFin != null ? dateFin : "null") + "," + montant;
    }
}

