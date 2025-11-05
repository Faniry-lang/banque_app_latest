package itu.banque.compte.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;

@Entity
@Table(name = "compte_courant")
public class CompteCourant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(name = "solde_initial", precision = 15, scale = 2)
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(name = "date_creation")
    private LocalDate dateCreation = LocalDate.now();

    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL)
    private List<TransactionCourant> transactions;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public BigDecimal getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(BigDecimal soldeInitial) { this.soldeInitial = soldeInitial; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public List<TransactionCourant> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionCourant> transactions) { this.transactions = transactions; }

    public CompteCourantDto toDto(CompteCourantDto dto) {
        Integer id = 
    }
}
