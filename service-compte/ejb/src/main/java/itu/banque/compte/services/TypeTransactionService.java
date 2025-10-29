package itu.banque.compte.services;

import java.util.List;

import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.remote.TypeTransactionServiceRemote;
import itu.banque.compte.daos.TypeTransactionDao;
import itu.banque.compte.entities.TypeTransaction;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class TypeTransactionService implements TypeTransactionServiceRemote {

    @EJB
    TypeTransactionDao typeTransactionDao;

    @Override
    public List<TypeTransactionDto> getAll() {
        List<TypeTransaction> types = typeTransactionDao.findAll();
        return types.stream()
                      .map(this::toDto)
                      .toList();
    }

    public TypeTransactionDto toDto(TypeTransaction entity) {
        TypeTransactionDto dto = new TypeTransactionDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
