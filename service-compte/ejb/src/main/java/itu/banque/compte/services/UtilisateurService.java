package itu.banque.compte.services;

import java.util.ArrayList;
import java.util.List;

import itu.banque.api.dtos.UtilisateurDto;
import itu.banque.api.remote.UtilisateurServiceRemote;
import itu.banque.compte.daos.persistence.PersistenceObjectManager;
import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import itu.banque.compte.entities.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class UtilisateurService implements UtilisateurServiceRemote {

    @Inject
    PersistenceObjectManager pom;

    @Override
    public UtilisateurDto login(String nom, String motDePasse) {

        Criterion nomMatch = new Criterion("nom", nom, Operator.EQUALS);
        Criterion motDePasseMatch = new Criterion("motDePasse", motDePasse, Operator.EQUALS);

        List<Utilisateur> resultats = pom.findByCriteria(
                                            Utilisateur.class, 
                                            new ArrayList<>(List.of(nomMatch, motDePasseMatch)));    
                                            
        UtilisateurDto utilisateurAuthentifié = null;
                                            
        if(resultats.size() == 1)
        {
            utilisateurAuthentifié = resultats.get(0).toDto();
        }

        return utilisateurAuthentifié;
    }
    
}
