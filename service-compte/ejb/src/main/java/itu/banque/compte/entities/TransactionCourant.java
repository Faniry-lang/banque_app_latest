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

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(LocalDate dateTransaction) { this.dateTransaction = dateTransaction; }
}
