package itu.banque.compte.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import itu.banque.api.dtos.UtilisateurDto;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(name = "id_direction", nullable = false)
    private Integer idDirection;

    @Column(name = "role_lvl", nullable = false)
    private Integer roleLvl;

    @Column(name = "mot_de_passe", nullable = false, length = 100)
    private String motDePasse;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getIdDirection() { return idDirection; }
    public void setIdDirection(Integer idDirection) { this.idDirection = idDirection; }

    public Integer getRoleLvl() { return roleLvl; }
    public void setRoleLvl(Integer roleLvl) { this.roleLvl = roleLvl; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public UtilisateurDto toDto()
    {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(id);
        dto.setIdDirection(idDirection);
        dto.setNom(nom);
        dto.setRoleLvl(roleLvl);

        return dto;
    }
}
