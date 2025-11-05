package itu.banque.compte.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import itu.banque.api.dtos.VirementDto;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "virement")
public class Virement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_compte", nullable = false)
    private CompteCourant compte;

    @ManyToOne
    @JoinColumn(name = "id_compte_beneficiaire", nullable = false)
    private CompteCourant compteBeneficiaire;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "devise_ref")
    private Integer deviseRef;

    @Column(name = "date_virement", nullable = false)
    private LocalDate dateVirement = LocalDate.now();

    @Column(name = "total_frais")
    private BigDecimal totalFrais;

    public Virement(Integer id, CompteCourant compte, CompteCourant compteBeneficiaire, BigDecimal montant,
            Integer deviseRef, LocalDate dateVirement, BigDecimal totalFrais) {
        this.setId(id);
        this.setCompte(compte);
        this.setCompteBeneficiaire(compteBeneficiaire);
        this.setMontant(montant);
        this.setDeviseRef(deviseRef);
        this.setDateVirement(dateVirement);
        this.setTotalFrais(totalFrais);
    }

    protected Virement() {}

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

    public CompteCourant getCompteBeneficiaire() {
        return compteBeneficiaire;
    }

    private void setCompteBeneficiaire(CompteCourant compteBeneficiaire) {
        if(compteBeneficiaire == null)
        {
            throw new IllegalArgumentException("Le compte bénéficiaire du virement ne peut pas être nul");
        }
        this.compteBeneficiaire = compteBeneficiaire;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        if(montant == null)
        {
            throw new IllegalArgumentException("Le montant ne peut pas être nul");
        }
        if(montant.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Le montant du virement ne peut pas être inférieur ou égal à 0");
        }
        this.montant = montant;
    }

    public Integer getDeviseRef() {
        return deviseRef;
    }

    private void setDeviseRef(Integer deviseRef) {
        this.deviseRef = deviseRef;
    }

    public LocalDate getDateVirement() {
        return dateVirement;
    }

    private void setDateVirement(LocalDate dateVirement) {
        if(dateVirement.isAfter(LocalDate.now()))
        {
            throw new IllegalArgumentException("La date du virement ne peut pas être inférieur à la date actuelle");
        }
        this.dateVirement = dateVirement;
    }

    public BigDecimal getTotalFrais() {
        return totalFrais;
    }

    public void setTotalFrais(BigDecimal totalFrais) {
        this.totalFrais = totalFrais;
    }

    public VirementDto toDto()
    {
        VirementDto dto = new VirementDto();
        dto.setId(id);
        dto.setIdCompte(compte.getId());
        dto.setIdCompteBeneficiaire(compteBeneficiaire.getId());
        dto.setMontant(montant);
        dto.setDeviseRef(deviseRef);
        dto.setDateVirement(dateVirement);
        dto.setTotalFrais(totalFrais);

        return dto;
    }

    public static Virement fromDto(PersistenceObjectManager pom, VirementDto dto)
    {
        Integer id = dto.getId();
        CompteCourant compte = pom.findById(dto.getIdCompte(), CompteCourant.class);
        CompteCourant compteBeneficiaire = pom.findById(dto.getIdCompteBeneficiaire(), CompteCourant.class);
        BigDecimal montant = dto.getMontant();
        Integer deviseRef = dto.getDeviseRef();
        LocalDate dateVirement = dto.getDateVirement();
        BigDecimal totalFrais = dto.getTotalFrais();

        return new Virement(id, compte, compteBeneficiaire, montant, deviseRef, dateVirement, totalFrais);
    }

    public boolean estValide(PersistenceObjectManager pom, LocalDate date) {
        if(date == null) throw new IllegalArgumentException("La date ne peut pas être nulle");

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("tableReference", "virement", Operator.EQUALS));
        criteria.add(new Criterion("idReference", this.id, Operator.EQUALS));
        criteria.add(new Criterion("dateValidation", date, Operator.LESS_OR_EQUALS));

        List<Validation> resultats = Optional.ofNullable(
                pom.findByCriteria(Validation.class, criteria)
            ).orElse(List.of());

        Optional<Validation> latest = resultats.stream()
                .max(Comparator
                        .comparing(Validation::getDateValidation)
                        .thenComparing(Validation::getId)
                );

        return latest.map(v -> !v.getDateValidation().isAfter(date.atStartOfDay()))
                    .orElse(false);
    }

    public Validation valider(Utilisateur utilisateur, LocalDateTime dateValidation)
    {
        if(dateValidation == null)
        {
            dateValidation = LocalDateTime.now();
        }
        return new Validation(null, id, "virement", utilisateur, dateValidation);
    }

    public Etat changerEtat(Integer etatNum, LocalDateTime dateEtat)
    {
        if(dateEtat == null)
        {
            dateEtat = LocalDateTime.now();
        }
        return new Etat(null, id, "virement", etatNum, dateEtat);
    }
}
