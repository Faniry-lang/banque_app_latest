package itu.banque.compte.services;

import java.util.List;

import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.remote.TransactionCourantServiceRemote;
import itu.banque.compte.daos.CompteCourantDao;
import itu.banque.compte.daos.TransactionCourantDao;
import itu.banque.compte.daos.TypeTransactionDao;
import itu.banque.compte.entities.TransactionCourant;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class TransactionCourantService implements TransactionCourantServiceRemote {

    @EJB
    TransactionCourantDao transactionCourantDao;

    @EJB
    CompteCourantDao compteCourantDao;

    @EJB
    TypeTransactionDao typeTransactionDao;

    @Override
    public List<TransactionCourantDto> getByCompteId(Integer compteId) {
        List<TransactionCourant> transactions = transactionCourantDao.getByCompteId(compteId);
        return transactions.stream()
                           .map(this::toDto)
                           .toList();
    }

    @Override
    public List<TransactionCourantDto> getAll() {
        List<TransactionCourant> transactions = transactionCourantDao.findAll();
        return transactions.stream()
                           .map(this::toDto)
                           .toList();
    }

    @Override
    public TransactionCourantDto create(TransactionCourantDto transactionCourantDto) {
        TransactionCourant transactionCourant = transactionCourantDao.save(fromDto(transactionCourantDto));
        return toDto(transactionCourant);
    }

    public TransactionCourant fromDto(TransactionCourantDto dto) {
        TransactionCourant entity = new TransactionCourant();
        entity.setId(dto.getId());
        entity.setCompte(compteCourantDao.findById(dto.getIdCompte()));
        entity.setTypeTransaction(typeTransactionDao.findById(dto.getIdTypeTransaction()));
        entity.setMontant(dto.getMontant());
        entity.setDateTransaction(dto.getDateTransaction());
        return entity;
    }

    public TransactionCourantDto toDto(TransactionCourant entity) {
        TransactionCourantDto dto = new TransactionCourantDto();
        dto.setId(entity.getId());
        dto.setIdCompte(entity.getCompte().getId());
        dto.setIdTypeTransaction(entity.getTypeTransaction().getId());
        dto.setMontant(entity.getMontant());
        dto.setDateTransaction(entity.getDateTransaction());
        return dto;
    }
    
}
