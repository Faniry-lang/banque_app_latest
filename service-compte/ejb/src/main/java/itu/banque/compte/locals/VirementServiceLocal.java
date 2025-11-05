package itu.banque.compte.locals;

import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.dtos.VirementDto;
import itu.banque.compte.entities.Virement;
import jakarta.ejb.Local;

@Local
public interface VirementServiceLocal {
    VirementDto valider(VirementDto virementDto, Integer idUtilisateur) throws Exception ;
    VirementDto annuler(VirementDto virementDto, Integer idUtilisateur) throws Exception ;
    VirementDto toDto(Virement virement);
    Virement fromDto(VirementDto dto);
}
