package itu.banque.compte.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;

@Entity
@Table(name = "transaction_courant")
public class TransactionCourant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private CompteCourant compte;

    @ManyToOne
    @JoinColumn(name = "type_transaction", nullable = false)
    private TypeTransaction typeTransaction;

    @ManyToOne
    @JoinColumn(name = "id_virement_source")
    private Virement virementSource;

    @Column(name = "devise_ref")
    private Integer deviseRef;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction")
    private LocalDate dateTransaction = LocalDate.now();

    public TransactionCourant() {
    }

    public TransactionCourant(Integer id, CompteCourant compte, TypeTransaction typeTransaction,
            Virement virementSource, Integer deviseRef, BigDecimal montant, LocalDate dateTransaction) {
        this.setId(id);
        this.setCompte(compte);
        this.setTypeTransaction(typeTransaction);
        this.setVirementSource(virementSource);
        this.setDeviseRef(deviseRef);
        this.setMontant(montant);
        this.setDateTransaction(dateTransaction);
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public CompteCourant getCompte() {
        return compte;
    }

    private void setCompte(CompteCourant compte) {
        if(compte == null)
        {
            throw new IllegalArgumentException("Le compte émetteur du virement ne peut pas être nul");
        }
        this.compte = compte;
    }

    public TypeTransaction getTypeTransaction() {
        return typeTransaction;
    }

    private void setTypeTransaction(TypeTransaction typeTransaction) {
        if(typeTransaction == null)
        {
            throw new IllegalArgumentException("Le type de transaction de la transaction ne peut pas être nul");
        }
        this.typeTransaction = typeTransaction;
    }

    public Virement getVirementSource() {
        return virementSource;
    }

    private void setVirementSource(Virement virementSource) {
        this.virementSource = virementSource;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }

    private void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    private void setMontant(BigDecimal montant) {
        if(montant == null)
        {
            throw new IllegalArgumentException("Le montant ne peut pas être nul");
        }
        if(montant.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Le montant de la transaction ne peut pas être inférieur ou égal à 0");
        }
        this.montant = montant;
    }

    public LocalDate getDateTransaction() {
        return dateTransaction;
    }

    private void setDateTransaction(LocalDate dateTransaction) {
        if(dateTransaction.isAfter(LocalDate.now()))
        {
            throw new IllegalArgumentException("La date du virement ne peut pas être inférieur à la date actuelle");
        }
        this.dateTransaction = dateTransaction;
    }

    public TransactionCourantDto toDto()
    {
        TransactionCourantDto dto = new TransactionCourantDto();
        dto.setId(id);
        dto.setIdCompte(compte.getId());
        dto.setIdTypeTransaction(typeTransaction.getId());
        dto.setTypeTransactionStr(typeTransaction.getNom());
        dto.setIdVirementSource(virementSource != null ? virementSource.getId() : null);
        dto.setMontant(montant);
        dto.setDeviseRef(deviseRef);
        dto.setDateTransaction(dateTransaction);

        return dto;
    }

    public boolean estValide(PersistenceObjectManager pom, LocalDate date) {
        if(date == null) throw new IllegalArgumentException("La date ne peut pas être nulle");

        LocalDateTime dateTime =  date.atTime(23, 59, 59);

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("tableReference", "transaction_courant", Operator.EQUALS));
        criteria.add(new Criterion("idReference", this.id, Operator.EQUALS));
        criteria.add(new Criterion("dateValidation", dateTime, Operator.LESS_OR_EQUALS));

        List<Validation> resultats = Optional.ofNullable(
                pom.findByCriteria(Validation.class, criteria)
            ).orElse(List.of());

        Optional<Validation> latest = resultats.stream()
                .max(Comparator
                        .comparing(Validation::getDateValidation)
                        .thenComparing(Validation::getId)
                );

        return latest.map(v -> !v.getDateValidation().isAfter(dateTime))
                    .orElse(false);
    }

    public Validation valider(Utilisateur utilisateur, LocalDateTime dateValidation)
    {
        if(dateValidation == null)
        {
            dateValidation = LocalDateTime.now();
        }
        return new Validation(null, id, "transaction_courant", utilisateur, dateValidation);
    }
}
