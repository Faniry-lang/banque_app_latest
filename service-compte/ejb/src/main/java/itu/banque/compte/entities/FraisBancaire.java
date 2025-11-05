package itu.banque.compte.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import itu.banque.api.dtos.FraisBancaireDto;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "frais_bancaire")
public class FraisBancaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "montant_inf")
    BigDecimal montantInf;

    @Column(name = "montant_sup")
    BigDecimal montantSup;

    @Column(name = "frais_forfaitaire")
    BigDecimal fraisForfaitaire;

    @Column(name = "frais_pourcentage")
    BigDecimal fraisPourcentage;

    @Column(name = "date_frais")
    LocalDate dateFrais;

    public FraisBancaire(Integer id, BigDecimal montantInf, BigDecimal montantSup, BigDecimal fraisForfaitaire,
            BigDecimal fraisPourcentage, LocalDate dateFrais) {
        this.setId(id);
        this.setMontantInf(montantInf);
        this.setMontantSup(montantSup);
        this.setFraisForfaitaire(fraisForfaitaire);
        this.setFraisPourcentage(fraisPourcentage);
        this.setDateFrais(dateFrais);

        if(montantInf.compareTo(montantSup) >= 0)
        {
            throw new IllegalArgumentException("Le montant inférieur ne peut pas être supé");
        }

        if(
            (
                fraisForfaitaire == null || 
                fraisForfaitaire.compareTo(BigDecimal.ZERO) <= 0
            ) && 
            (
                fraisPourcentage == null || 
                fraisPourcentage.compareTo(BigDecimal.ZERO) <= 0
            )
        ) {
            throw new IllegalArgumentException("Le frais forfaitaire et le frais pourcentage ne peuvent pas être nuls");
        }
    }

    public FraisBancaire() {
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontantInf() {
        return montantInf;
    }

    private void setMontantInf(BigDecimal montantInf) {
        if(montantInf == null)
        {
            throw new IllegalArgumentException("Le montant inf ne peut pas être nul");
        }
        if(montantInf.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Le montant inf ne peut pas être inférieur ou égal à zéro");
        }
        this.montantInf = montantInf;
    }

    public BigDecimal getMontantSup() {
        return montantSup;
    }

    private void setMontantSup(BigDecimal montantSup) {
        if(montantSup == null)
        {
            throw new IllegalArgumentException("Le montant sup ne peut pas être nul");
        }
        if(montantSup.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Le montant sup ne peut pas être inférieur ou égal à zéro");
        }
        this.montantSup = montantSup;
    }

    public BigDecimal getFraisForfaitaire() {
        return fraisForfaitaire;
    }

    private void setFraisForfaitaire(BigDecimal fraisForfaitaire) {
        this.fraisForfaitaire = fraisForfaitaire;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    private void setFraisPourcentage(BigDecimal fraisPourcentage) {
        this.fraisPourcentage = fraisPourcentage;
    }

    public LocalDate getDateFrais() {
        return dateFrais;
    }

    private void setDateFrais(LocalDate dateFrais) {
        this.dateFrais = dateFrais;
    }

    public FraisBancaireDto toDto()
    {
        FraisBancaireDto dto = new FraisBancaireDto();
        dto.setId(id);
        dto.setMontantInf(montantInf);
        dto.setMontantSup(montantSup);
        dto.setFraisForfaitaire(fraisForfaitaire);
        dto.setFraisPourcentage(fraisPourcentage);
        dto.setDateFrais(dateFrais);

        return dto;
    }

    public FraisBancaire fromDto(FraisBancaireDto dto)
    {
        Integer id = dto.getId();
        BigDecimal montantInf = dto.getMontantInf();
        BigDecimal montantSup = dto.getMontantSup();
        BigDecimal fraisForfaitaire = dto.getFraisForfaitaire();
        BigDecimal fraisPourcentage = dto.getFraisPourcentage();
        LocalDate dateFrais = dto.getDateFrais();

        FraisBancaire fraisBancaire = new FraisBancaire(id, montantInf, montantSup, fraisForfaitaire, fraisPourcentage, dateFrais);

        return fraisBancaire;
    }

    public static FraisBancaire getPourMontant(PersistenceObjectManager pom, BigDecimal montant, LocalDate date) {
        if (montant == null || date == null) {
            throw new IllegalArgumentException("Le montant et la date ne peuvent pas être nuls");
        }

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("montantInf", montant, Operator.LESS_OR_EQUALS));
        criteria.add(new Criterion("montantSup", montant, Operator.GREATER_OR_EQUALS));
        criteria.add(new Criterion("dateFrais", date, Operator.LESS_OR_EQUALS));

        List<FraisBancaire> resultats = Optional.ofNullable(
                pom.findByCriteria(FraisBancaire.class, criteria)
            ).orElse(List.of());

        return resultats.stream()
                .max(Comparator
                        .comparing(FraisBancaire::getDateFrais)
                        .thenComparing(FraisBancaire::getId))
                .orElse(null);
    }


    public BigDecimal getTotalFrais(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement supérieur à zéro.");
        }

        if(montant.compareTo(this.montantInf) < 0 || montant.compareTo(this.montantSup) > 0)
        {
            throw new IllegalArgumentException("Le montant n'est pas pris en charge par l'intervalle du frais bancaire");
        }

        if ((this.fraisForfaitaire == null || this.fraisForfaitaire.compareTo(BigDecimal.ZERO) <= 0)
                && (this.fraisPourcentage == null || this.fraisPourcentage.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException(
                    "Frais forfaitaire et frais pourcentage ne peuvent pas être tous nuls ou inférieurs/égaux à zéro.");
        }

        BigDecimal fraisForfaitaireValue = BigDecimal.ZERO;
        if (this.fraisForfaitaire != null) {
            if (this.fraisForfaitaire.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le frais forfaitaire doit être supérieur à zéro si défini.");
            }
            fraisForfaitaireValue = this.fraisForfaitaire;
        }

        BigDecimal fraisPourcentageValue = BigDecimal.ZERO;
        if (this.fraisPourcentage != null) {
            if (this.fraisPourcentage.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le frais pourcentage doit être supérieur à zéro si défini.");
            }
            fraisPourcentageValue = montant.multiply(
                    this.fraisPourcentage.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        }

        return fraisForfaitaireValue.add(fraisPourcentageValue);
    }

}
