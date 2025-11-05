package itu.banque.compte.daos;

import itu.banque.compte.entities.Virement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VirementDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public Virement create(Virement virement) {
        em.persist(virement);
        return virement;
    }

    public Virement findById(Integer id) {
        return em.find(Virement.class, id);
    }

    public List<Virement> findAll() {
        return em.createQuery("SELECT e FROM Virement e", Virement.class).getResultList();
    }

    public Virement update(Virement virement) {
        return em.merge(virement);
    }

    public void delete(Integer id) {
        Virement virement = findById(id);
        if (virement != null) {
            em.remove(virement);
        }
    }

    public Virement findByTransactionId(Integer transactionId) {
        try {
            return em.createQuery("SELECT v FROM Virement v WHERE v.transactionEntree.id = :txId OR v.transactionSortie.id = :txId", Virement.class)
                     .setParameter("txId", transactionId)
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
