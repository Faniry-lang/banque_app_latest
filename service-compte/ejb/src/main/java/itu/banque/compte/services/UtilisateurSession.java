package itu.banque.compte.services;

import java.util.List;

import itu.banque.api.dtos.ActionRoleDto;
import itu.banque.api.dtos.DirectionDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.remote.UtilisateurSessionRemote;
import itu.banque.compte.daos.UtilisateurDao;
import itu.banque.compte.entities.Utilisateur;
import jakarta.ejb.EJB;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.SessionScoped;

@Stateful
public class UtilisateurSession implements UtilisateurSessionRemote {

    @EJB
    UtilisateurDao utilisateurDao;

    UtilisateurDto utilisateur;
    List<ActionRoleDto> actionsRoles;
    
    List<DirectionDto> directions;

    @Override
    public UtilisateurDto getUtilisateur() {
        return this.utilisateur;
    }

    @Override
    public List<ActionRoleDto> getActionsRoles() {
        return this.actionsRoles;
    }

    @Override
    public List<DirectionDto> getDirections() {
        return this.directions;
    }

    @Override
    public void setActionsRoles(List<ActionRoleDto> actionsRoles) {
        this.actionsRoles = actionsRoles;
    }

    @Override
    public void setDirections(List<DirectionDto> directions) {
        this.directions = directions;
    }

    public UtilisateurDto toDto(Utilisateur entity) {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setIdDirection(entity.getIdDirection());
        dto.setRoleLvl(entity.getRoleLvl());
        return dto;
    }

    @Override
    public void setUtilisateur(UtilisateurDto utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public boolean canPerformAction(String nomTable, String action) {
        if (this.utilisateur == null || this.actionsRoles == null) {
            return false;
        }

        int userRoleLvl = this.utilisateur.getRoleLvl();

        for (ActionRoleDto actionRole : this.actionsRoles) {
            if (actionRole.getNomTable().equalsIgnoreCase(nomTable) &&
                actionRole.getAction().equalsIgnoreCase(action) &&
                actionRole.getRoleLvl() <= userRoleLvl) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Remove
    public void destroy() {
        this.utilisateur = null;
        this.actionsRoles = null;
        this.directions = null;
    }
    
}
