package itu.banque.api.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TransactionInvalideException extends Exception {
    public TransactionInvalideException(String message)
    {
        super(message);
    }
}
