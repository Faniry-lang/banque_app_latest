package itu.banque.pret.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mensualite")
public class Mensualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "montant_capital", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantCapital;

    @Column(name = "montant_interet", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantInteret;

    @Column(name = "date_mensualite", nullable = false)
    private LocalDate dateMensualite;

    @ManyToOne
    @JoinColumn(name = "id_contrat", nullable = false)
    private ContratPret contrat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontantCapital() {
        return montantCapital;
    }

    public void setMontantCapital(BigDecimal montantCapital) {
        this.montantCapital = montantCapital;
    }

    public BigDecimal getMontantInteret() {
        return montantInteret;
    }

    public void setMontantInteret(BigDecimal montantInteret) {
        this.montantInteret = montantInteret;
    }

    public LocalDate getDateMensualite() {
        return dateMensualite;
    }

    public void setDateMensualite(LocalDate dateMensualite) {
        this.dateMensualite = dateMensualite;
    }

    public ContratPret getContrat() {
        return contrat;
    }

    public void setContrat(ContratPret contrat) {
        this.contrat = contrat;
    }
}
