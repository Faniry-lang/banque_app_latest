package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.ActionRoleDto;
import itu.banque.api.dtos.DirectionDto;
import itu.banque.api.dtos.UtilisateurDto;
import jakarta.ejb.Remote;

@Remote
public interface UtilisateurSessionRemote {
    UtilisateurDto getUtilisateur();
    void setUtilisateur(UtilisateurDto utilisateur);
    List<ActionRoleDto> getActionsRoles();
    void setActionsRoles(List<ActionRoleDto> actionsRoles);
    List<DirectionDto> getDirections();
    void setDirections(List<DirectionDto> directions);
    boolean canPerformAction(String nomTable, String action);
    void destroy();
}
