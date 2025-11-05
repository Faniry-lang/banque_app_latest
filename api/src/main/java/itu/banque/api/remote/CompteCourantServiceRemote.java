package itu.banque.api.remote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.CompteCourantDto;
import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.TransactionCourantDto;
import itu.banque.api.dtos.TypeTransactionDto;
import itu.banque.api.dtos.VirementDto;
import jakarta.ejb.Remote;

@Remote
public interface CompteCourantServiceRemote {
    List<CompteCourantDto> getAll();
    VirementDto virer(VirementDto virementDto) throws Exception;
    CompteCourantDto findById(Integer id);
    TransactionCourantDto effectuerTransaction(TransactionCourantDto dto) throws Exception ;
    BigDecimal getSolde(LocalDate date, Integer compteId);
    List<TransactionCourantDto> getAllTransactions(Integer compteId);

    VirementDto validerVirement(Integer virementId, Integer utilisateurId);
    TransactionCourantDto validerTransaction(Integer transactionId, Integer utilisateurId);
    List<VirementDto> getVirementsForCompte(Integer compteId);
    List<TypeTransactionDto> getAllTypeTransactions();
    List<Devise> getAllDevises() throws Exception;
}

