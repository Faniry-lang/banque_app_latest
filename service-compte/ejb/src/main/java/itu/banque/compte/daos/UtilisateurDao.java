package itu.banque.compte.daos;

import itu.banque.compte.entities.Utilisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UtilisateurDao {
    
    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    public Utilisateur save(Utilisateur utilisateur) {
        em.persist(utilisateur);
        return utilisateur;
    }

    public Utilisateur findById(Integer id) {
        return em.find(Utilisateur.class, id);
    }

    public Utilisateur findByNomEtMotDePasse(String nom, String motDePasse) {
        try {
            return em.createQuery("SELECT u FROM Utilisateur u WHERE u.nom = :nom AND u.motDePasse = :motDePasse", Utilisateur.class)
                     .setParameter("nom", nom)
                     .setParameter("motDePasse", motDePasse)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Utilisateur update(Utilisateur utilisateur) {
        return em.merge(utilisateur);
    }

    public void delete(Utilisateur utilisateur) {
        em.remove(em.contains(utilisateur) ? utilisateur : em.merge(utilisateur));
    }
}
