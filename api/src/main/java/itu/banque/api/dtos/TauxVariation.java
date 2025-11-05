package itu.banque.api.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class TauxVariation implements Serializable {
    private Integer ref;
    private double pourcentage;
    private String deviseName;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public TauxVariation() {
    }

    public TauxVariation(Integer ref, double pourcentage, String deviseName, LocalDate dateDebut, LocalDate dateFin) {
        this.ref = ref;
        this.pourcentage = pourcentage;
        this.deviseName = deviseName;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
    }

    public String getDeviseName() {
        return deviseName;
    }

    public void setDeviseName(String deviseName) {
        this.deviseName = deviseName;
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
}
