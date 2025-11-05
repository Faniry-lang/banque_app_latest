package itu.banque.compte.daos;

import itu.banque.compte.entities.ContexteTransaction;
import itu.banque.compte.entities.FrequencePlafond;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class FrequencePlafondDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public FrequencePlafond create(FrequencePlafond frequencePlafond) {
        em.persist(frequencePlafond);
        return frequencePlafond;
    }

    public FrequencePlafond findById(Integer id) {
        return em.find(FrequencePlafond.class, id);
    }

    public List<FrequencePlafond> findAll() {
        return em.createQuery("SELECT e FROM FrequencePlafond e", FrequencePlafond.class).getResultList();
    }

    public FrequencePlafond findByLibelle(String libelle)
    {
        try {
            return em.createQuery("SELECT e FROM FrequencePlafond e WHERE e.libelle = :libelle", FrequencePlafond.class)
                        .setParameter("libelle", libelle)
                        .setMaxResults(1)
                        .getSingleResult();
        } catch(NoResultException e)
        {
            throw e;
        }
    }

    public FrequencePlafond update(FrequencePlafond frequencePlafond) {
        return em.merge(frequencePlafond);
    }

    public void delete(Integer id) {
        FrequencePlafond frequencePlafond = findById(id);
        if (frequencePlafond != null) {
            em.remove(frequencePlafond);
        }
    }
}
