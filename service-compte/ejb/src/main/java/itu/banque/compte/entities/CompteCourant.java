package itu.banque.compte.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;

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
    private List<TransactionCourant> transactions = new ArrayList<>();

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

    public CompteCourantDto toDto()
    {
        CompteCourantDto dto = new CompteCourantDto();
        dto.setId(id);
        dto.setIdClient(client.getId());
        dto.setSoldeInitial(soldeInitial);
        dto.setDateCreation(dateCreation);
        dto.setTransactions(transactions != null ? 
            transactions.stream().map(TransactionCourant::toDto).collect(Collectors.toList()) : List.of());

        return dto;
    }

    public BigDecimal getSolde(PersistenceObjectManager pom, LocalDate date)
    {
        Criterion dateCriteria = new Criterion("dateTransaction", date, Operator.LESS_OR_EQUALS);
        Criterion compteCriteria = new Criterion("compte.id", this.id, Operator.EQUALS);

        List<TransactionCourant> transactionCourants = pom.findByCriteria(
                                                        TransactionCourant.class,
                                                        new ArrayList<>(List.of(dateCriteria, compteCriteria)));

        System.out.println("[DEBUG function getSolde]");
        System.out.println("Debug de la liste des transactions courantes");
        for(TransactionCourant tr : transactionCourants)
        {
            System.out.println(tr.getMontant());
        }

        List<TransactionCourant> transactionCourantsValides = transactionCourants.stream().filter(t -> t.estValide(pom, date)).collect(Collectors.toList());

        BigDecimal solde = this.getSoldeInitial() != null ? this.getSoldeInitial() : BigDecimal.ZERO;

        for(TransactionCourant transactionCourant : transactionCourantsValides)
        {
            switch (transactionCourant.getTypeTransaction().getNom()) {
                case "DEBIT":
                    solde = solde.subtract(transactionCourant.getMontant());
                    break;
                case "CREDIT":
                    solde = solde.add(transactionCourant.getMontant());
                    break;
                default:
                    break;
            }
        }

        return solde;
    }

    public List<Virement> getVirementsJournaliers(PersistenceObjectManager pom, LocalDate date)
    {
        Criterion compteCriteria = new Criterion("compte.id", this.id, Operator.EQUALS);
        Criterion dateCriteria = new Criterion("dateVirement", date, Operator.EQUALS);
        List<Criterion> criterions = new ArrayList<>(List.of(compteCriteria, dateCriteria));

        List<Virement> resultats = Optional.ofNullable(
                                        pom.findByCriteria(Virement.class, criterions)
                                    ).orElse(List.of());

        return resultats.stream().filter(v -> v.estValide(pom, date)).collect(Collectors.toList());
    }
}
