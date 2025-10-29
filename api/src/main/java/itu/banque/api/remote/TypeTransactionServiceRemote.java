package itu.banque.api.remote;

import java.util.List;

import itu.banque.api.dtos.TypeTransactionDto;
import jakarta.ejb.Remote;

@Remote
public interface TypeTransactionServiceRemote {
    List<TypeTransactionDto> getAll();
}
