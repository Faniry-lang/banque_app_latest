package itu.banque.compte.services;

import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.entities.CompteCourant;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class CompteCourantService implements CompteCourantServiceRemote {

    @EJB
    CompteCourantDao compteCourantDao;

    @Override
    public List<CompteCourantDto> getAll() {
        List<CompteCourant> comptes = compteCourantDao.findAll();
        return comptes.stream()
                      .map(this::toDto)
                      .toList();
    }

    public CompteCourantDto toDto(CompteCourant entity) {
        CompteCourantDto dto = new CompteCourantDto();
        dto.setId(entity.getId());
        dto.setIdClient(entity.getClient().getId());
        dto.setDateCreation(entity.getDateCreation());
        dto.setSoldeInitial(entity.getSoldeInitial());
        return dto;
    }
    
}
