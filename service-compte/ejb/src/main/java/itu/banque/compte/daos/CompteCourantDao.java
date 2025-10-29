package itu.banque.compte.daos;

import itu.banque.compte.entities.CompteCourant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CompteCourantDao {

    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    public CompteCourant save(CompteCourant compte) {
        em.persist(compte);
        return compte;
    }

    public CompteCourant findById(Integer id) {
        return em.find(CompteCourant.class, id);
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    public CompteCourant update(CompteCourant compte) {
        return em.merge(compte);
    }

    public void delete(CompteCourant compte) {
        em.remove(em.contains(compte) ? compte : em.merge(compte));
    }
}
