package itu.banque.api.remote;

import itu.banque.api.dtos.UtilisateurDto;
import jakarta.ejb.Remote;

@Remote
public interface UtilisateurServiceRemote {
    UtilisateurDto login(String nom, String motDePasse);
}
