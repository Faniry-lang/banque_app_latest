package itu.banque.compte.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "type_transaction")
public class TypeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public itu.banque.api.dtos.TypeTransactionDto toDto() {
        itu.banque.api.dtos.TypeTransactionDto dto = new itu.banque.api.dtos.TypeTransactionDto();
        dto.setId(this.getId());
        dto.setNom(this.getNom());
        dto.setDescription(this.getDescription());
        return dto;
    }
}
