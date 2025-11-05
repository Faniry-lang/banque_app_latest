package itu.banque.api.remote;

import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.dtos.VirementDto;
import jakarta.ejb.Remote;

@Remote
public interface VirementServiceRemote {
    VirementDto valider(VirementDto virementDto, Integer idUtilisateur) throws Exception ;
    VirementDto annuler(VirementDto virementDto, Integer idUtilisateur) throws Exception ;
    VirementDto findByTransactionId(Integer transactionId);
}
