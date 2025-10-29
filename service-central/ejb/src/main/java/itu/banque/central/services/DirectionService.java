package itu.banque.central.services;

import java.util.List;

import itu.banque.api.dtos.DirectionDto;
import itu.banque.api.remote.DirectionServiceRemote;
import itu.banque.central.daos.DirectionDao;
import itu.banque.central.entities.Direction;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class DirectionService implements DirectionServiceRemote {

    @EJB
    DirectionDao directionDao;

    @Override
    public List<DirectionDto> getAll() {
        List<Direction> directions = directionDao.findAll();
        return directions.stream()
                         .map(this::toDto)
                         .toList();
    }

    public DirectionDto toDto(Direction entity) {
        DirectionDto dto = new DirectionDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        return dto;
    }
    
}
