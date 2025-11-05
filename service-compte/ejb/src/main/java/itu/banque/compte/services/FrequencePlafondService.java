package itu.banque.compte.services;

import java.util.List;
import java.util.stream.Collectors;

import itu.banque.api.dtos.FrequencePlafondDto;
import itu.banque.api.remote.FrequencePlafondServiceRemote;
import itu.banque.compte.daos.FrequencePlafondDAO;
import itu.banque.compte.entities.FrequencePlafond;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class FrequencePlafondService implements FrequencePlafondServiceRemote {

    @EJB
    FrequencePlafondDAO frequencePlafondDAO;

    @Override
    public List<FrequencePlafondDto> getAll() {
        List<FrequencePlafond> frequencePlafonds = this.frequencePlafondDAO.findAll();
        return frequencePlafonds.stream().map(this::toDto).collect(Collectors.toList());
    }

    public FrequencePlafondDto toDto(FrequencePlafond entity)
    {
        Integer id = entity.getId();
        String libelle = entity.getLibelle();

        FrequencePlafondDto dto = new FrequencePlafondDto(id, libelle);

        return dto;
    }

    public FrequencePlafond fromDto(FrequencePlafondDto dto)
    {
        Integer id = dto.getId();
        String libelle = dto.getLibelle();

        FrequencePlafond frequencePlafond =  new FrequencePlafond(id, libelle);

        return frequencePlafond;

    }
    
}
