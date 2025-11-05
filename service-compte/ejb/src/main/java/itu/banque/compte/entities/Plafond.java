package itu.banque.compte.entities;

import java.math.BigDecimal;
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
@Table(name = "plafond")
public class Plafond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @ManyToOne
    @JoinColumn(name = "id_type_transaction", nullable = false)
    private TypeTransaction typeTransaction;

    @ManyToOne
    @JoinColumn(name = "id_frequence_plafond", nullable = false)
    private FrequencePlafond frequencePlafond;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private CompteCourant compte;

    @ManyToOne
    @JoinColumn(name = "id_contexte_transaction", nullable = false)
    private ContexteTransaction contexteTransaction;

    @Column(name = "date_debut", nullable = false)
    LocalDate dateDebut;

    @Column(name = "date_fin")
    LocalDate dateFin;

    public Plafond(Integer id, BigDecimal montant, TypeTransaction typeTransaction, FrequencePlafond frequencePlafond,
            ContexteTransaction contexteTransaction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.frequencePlafond = frequencePlafond;
        this.contexteTransaction = contexteTransaction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Plafond(Integer id, BigDecimal montant, TypeTransaction typeTransaction, FrequencePlafond frequencePlafond,
            CompteCourant compte, ContexteTransaction contexteTransaction, LocalDate dateDebut, LocalDate dateFin) {
        this.id = id;
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.frequencePlafond = frequencePlafond;
        this.compte = compte;
        this.contexteTransaction = contexteTransaction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Plafond() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(TypeTransaction typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public FrequencePlafond getFrequencePlafond() {
        return frequencePlafond;
    }

    public void setFrequencePlafond(FrequencePlafond frequencePlafond) {
        this.frequencePlafond = frequencePlafond;
    }

    public CompteCourant getCompte() {
        return compte;
    }

    public void setCompte(CompteCourant compte) {
        this.compte = compte;
    }

    public ContexteTransaction getContexteTransaction() {
        return contexteTransaction;
    }

    public void setContexteTransaction(ContexteTransaction contexteTransaction) {
        this.contexteTransaction = contexteTransaction;
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