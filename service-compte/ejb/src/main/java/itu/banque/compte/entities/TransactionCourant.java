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
    @JoinColumn(name = "id_contexte_transaction", nullable = false)
    private ContexteTransaction contexteTransaction;

    @ManyToOne
    @JoinColumn(name = "id_virement_source")
    private Virement virementSource;

    @Column(name = "devise_ref")
    private Integer deviseRef;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction")
    private LocalDate dateTransaction = LocalDate.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CompteCourant getCompte() { return compte; }
    public void setCompte(CompteCourant compte) { this.compte = compte; }

    public TypeTransaction getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(TypeTransaction typeTransaction) { this.typeTransaction = typeTransaction; }

    public ContexteTransaction getContexteTransaction() {
        return contexteTransaction;
    }
    public void setContexteTransaction(ContexteTransaction contexteTransaction) {
        this.contexteTransaction = contexteTransaction;
    }

    public Virement getVirementSource() {
        return virementSource;
    }
    public void setVirementSource(Virement virementSource) {
        this.virementSource = virementSource;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }
    public void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { 
        if(montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) 
        {
            throw new IllegalArgumentException("Montant du virement invalide");
        }
        this.montant = montant; 
    }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }
}
