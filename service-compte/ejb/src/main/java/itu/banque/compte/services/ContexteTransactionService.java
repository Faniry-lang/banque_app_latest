package itu.banque.compte.services;

import java.util.List;
import java.util.stream.Collectors;

import itu.banque.api.dtos.ContexteTransactionDto;
import itu.banque.api.remote.ContexteTransactionServiceRemote;
import itu.banque.compte.daos.ContexteTransactionDAO;
import itu.banque.compte.entities.ContexteTransaction;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class ContexteTransactionService implements ContexteTransactionServiceRemote {

    @EJB
    ContexteTransactionDAO contexteTransactionDAO;

    @Override
    public List<ContexteTransactionDto> getAll()
    {
        List<ContexteTransaction> contexteTransaction = this.contexteTransactionDAO.findAll();
        return contexteTransaction.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ContexteTransactionDto findByLibelle(String libelle) {
        ContexteTransaction ct = this.contexteTransactionDAO.findByLibelle(libelle);
        if(ct == null)
        {
            return null;
        }

        return toDto(ct);
    }
    
    public ContexteTransactionDto toDto(ContexteTransaction entity)
    {
        ContexteTransactionDto dto = new ContexteTransactionDto();
        dto.setId(entity.getId());
        dto.setLibelle(entity.getLibelle());

        return dto;
    }
}
