package itu.banque.compte.services;

import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.remote.UtilisateurServiceRemote;
import itu.banque.compte.daos.UtilisateurDao;
import itu.banque.compte.entities.Utilisateur;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class UtilisateurService implements UtilisateurServiceRemote {
    
    @EJB
    UtilisateurDao utilisateurDao;

    @Override
    public UtilisateurDto login(String nom, String motDePasse) {
        Utilisateur utilisateur = utilisateurDao.findByNomEtMotDePasse(nom, motDePasse);
        if(utilisateur != null) {
            return toDto(utilisateur);
        }
        return null;
    }

    public UtilisateurDto toDto(Utilisateur entity) {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setIdDirection(entity.getIdDirection());
        dto.setRoleLvl(entity.getRoleLvl());
        return dto;
    }
}
