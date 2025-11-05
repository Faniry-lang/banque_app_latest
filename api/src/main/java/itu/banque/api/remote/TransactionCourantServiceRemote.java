package itu.banque.api.remote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.ResultObject;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.ui.TransactionCourantViewModel;
import jakarta.ejb.Remote;

@Remote
public interface TransactionCourantServiceRemote {
    List<TransactionCourantDto> getByCompteId(Integer compteId);
    List<TransactionCourantDto> update(List<TransactionCourantDto> dtos);    
    TransactionCourantDto findById(Integer id);
    List<TransactionCourantDto> getAll();
    TransactionCourantDto create(TransactionCourantDto transactionCourantDto);
    TransactionCourantDto valider(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws Exception ;
    TransactionCourantDto annuler(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws Exception ;
    TransactionCourantDto effectuerTransaction(TransactionCourantDto transactionCourantDto) throws Exception;
    BigDecimal getSolde(LocalDate date, Integer compteId);
    List<TransactionCourantViewModel> getTransactionViewModelsByCompteId(Integer compteId);
    List<TransactionCourantDto> getByDeviseRef(Integer deviseRef);
}
