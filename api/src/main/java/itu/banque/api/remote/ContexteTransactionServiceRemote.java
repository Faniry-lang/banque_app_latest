package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.ContexteTransactionDto;
import jakarta.ejb.Remote;

@Remote
public interface ContexteTransactionServiceRemote {
    List<ContexteTransactionDto> getAll();
    ContexteTransactionDto findByLibelle(String libelle);
}
