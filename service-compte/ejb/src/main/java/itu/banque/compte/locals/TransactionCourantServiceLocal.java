package itu.banque.compte.locals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.compte.entities.TransactionCourant;
import jakarta.ejb.Local;

@Local
public interface TransactionCourantServiceLocal {
    List<TransactionCourantDto> getByCompteId(Integer compteId);
    List<TransactionCourantDto> getAll();
    TransactionCourantDto create(TransactionCourantDto transactionCourantDto);
    TransactionCourantDto valider(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws Exception ;
    TransactionCourantDto annuler(TransactionCourantDto transactionCourantDto, Integer idUtilisateur) throws Exception ;
    TransactionCourantDto toDto(TransactionCourant entity);
    TransactionCourant fromDto(TransactionCourantDto dto);
    void checkDepassementPlafondMensuel(TransactionCourant transactionCourant) throws Exception ;
    void checkDepassementPlafondOperation(TransactionCourant transactionCourant) throws Exception ;
    TransactionCourantDto effectuerTransaction(TransactionCourantDto transactionCourantDto) throws Exception;
    BigDecimal getSolde(LocalDate date, Integer compteId);
    List<TransactionCourantDto> getAllByCompteId(Integer compteId);
}
