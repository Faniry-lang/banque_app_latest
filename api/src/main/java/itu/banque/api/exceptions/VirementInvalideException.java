package itu.banque.api.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VirementInvalideException extends Exception {
    public VirementInvalideException(String message)
    {
        super(message);
    }
}
