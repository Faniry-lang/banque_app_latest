package itu.banque.central.services;

import java.util.List;

import itu.banque.api.dtos.ActionRoleDto;
import itu.banque.api.remote.ActionRoleServiceRemote;
import itu.banque.central.daos.ActionRoleDao;
import itu.banque.central.entities.ActionRole;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class ActionRoleService implements ActionRoleServiceRemote {

    @EJB
    ActionRoleDao actionRoleDao;

    @Override
    public List<ActionRoleDto> getAll() {
        List<ActionRole> actionRoles = actionRoleDao.findAll();
        return actionRoles.stream()
                          .map(this::toDto)
                          .toList();
    }

    public ActionRoleDto toDto(ActionRole entity) {
        ActionRoleDto dto = new ActionRoleDto();
        dto.setId(entity.getId());
        dto.setNomTable(entity.getNomTable());
        dto.setAction(entity.getAction());
        dto.setRoleLvl(entity.getRoleLvl());
        return dto;
    }

    @Override
    public List<ActionRoleDto> findAllowedRoles(Integer roleLvl) {
        List<ActionRole> actionRoles = actionRoleDao.findAllowedRoles(roleLvl);
        return actionRoles.stream()
                          .map(this::toDto)
                          .toList();
    }   
    
}
