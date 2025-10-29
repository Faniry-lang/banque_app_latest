package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.TransactionCourantDto;
import jakarta.ejb.Remote;

@Remote
public interface TransactionCourantServiceRemote {
    List<TransactionCourantDto> getByCompteId(Integer compteId);
    List<TransactionCourantDto> getAll();
    TransactionCourantDto create(TransactionCourantDto transactionCourantDto);
}
