package itu.banque.compte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import jakarta.persistence.*;

@Entity
@Table(name = "plafond_journalier")
public class PlafondJournalier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private CompteCourant compte;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    public PlafondJournalier(Integer id, BigDecimal montant, CompteCourant compte,
                             LocalDate dateDebut, LocalDate dateFin) {
        setId(id);
        setMontant(montant);
        setCompte(compte);
        setDateDebut(dateDebut);
        setDateFin(dateFin);
    }

    protected PlafondJournalier() {}

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    private void setMontant(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }
        this.montant = montant;
    }

    public CompteCourant getCompte() {
        return compte;
    }

    private void setCompte(CompteCourant compte) {
        if (compte == null) {
            throw new IllegalArgumentException("Le compte ne peut pas être nul");
        }
        this.compte = compte;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    private void setDateDebut(LocalDate dateDebut) {
        if (dateDebut == null) {
            throw new IllegalArgumentException("La date de début ne peut pas être nulle");
        }
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    private void setDateFin(LocalDate dateFin) {
        if (dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        this.dateFin = dateFin;
    }

    public static PlafondJournalier getPlafondJournalier(PersistenceObjectManager pom, 
                                                    CompteCourant compte, 
                                                    LocalDate date) {

        List<Criterion> criterions = new ArrayList<>();
        criterions.add(new Criterion("compte.id", compte.getId(), Operator.EQUALS));
        criterions.add(new Criterion("dateDebut", date, Operator.LESS_OR_EQUALS));
        
        // Critère pour la date de fin : soit elle est nulle, soit elle est dans le futur ou aujourd'hui
        Criterion dateFinSup = new Criterion("dateFin", date, Operator.GREATER_OR_EQUALS);
        Criterion dateFinNull = new Criterion("dateFin", null, Operator.EQUALS, true); // Mettre en OR
        criterions.add(dateFinSup);
        criterions.add(dateFinNull);

        List<PlafondJournalier> resultats = Optional.ofNullable(
            pom.findByCriteria(PlafondJournalier.class, criterions)
        ).orElse(List.of());

        // Retourne le plus récent en cas de multiples correspondances
        return resultats.stream()
                .max(Comparator.comparing(PlafondJournalier::getDateDebut))
                .orElse(null);
    }
}

