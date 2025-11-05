package itu.banque.compte.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

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

}
