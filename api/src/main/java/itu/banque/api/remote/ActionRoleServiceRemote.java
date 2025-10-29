package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.ActionRoleDto;
import jakarta.ejb.Remote;

@Remote
public interface ActionRoleServiceRemote {
    List<ActionRoleDto> getAll();
    List<ActionRoleDto> findAllowedRoles(Integer roleLvl);
}

