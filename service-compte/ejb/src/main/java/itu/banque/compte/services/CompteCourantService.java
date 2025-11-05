package itu.banque.compte.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.CreerVirementDto;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.api.remote.CompteCourantServiceRemote;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class CompteCourantService implements CompteCourantServiceRemote {

    @Inject
    PersistenceObjectManager pom;

    @Override
    public List<CompteCourantDto> getAll() {
        
    }

    @Override
    public VirementDto virer(CreerVirementDto creerVirementDto) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'virer'");
    }

    @Override
    public CompteCourantDto findById(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public TransactionCourantDto effectuerTransaction(TransactionCourantDto dto) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'effectuerTransaction'");
    }

    @Override
    public BigDecimal getSolde(LocalDate date, Integer compteId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSolde'");
    }

    @Override
    public List<TransactionCourantDto> getAllTransactions(Integer compteId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllTransactions'");
    }
    
}
