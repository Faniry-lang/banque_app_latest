package itu.banque.compte.daos;

import itu.banque.compte.entities.TransactionCourant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TransactionCourantDao {

    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    public TransactionCourant save(TransactionCourant transaction) {
        em.persist(transaction);
        return transaction;
    }

    public TransactionCourant findById(Integer id) {
        return em.find(TransactionCourant.class, id);
    }

    public List<TransactionCourant> findAll() {
        return em.createQuery("SELECT t FROM TransactionCourant t", TransactionCourant.class).getResultList();
    }

    public TransactionCourant update(TransactionCourant transaction) {
        return em.merge(transaction);
    }

    public void delete(TransactionCourant transaction) {
        em.remove(em.contains(transaction) ? transaction : em.merge(transaction));
    }

    public List<TransactionCourant> getByCompteId(Integer compteId)
    {
        return em.createQuery("SELECT t FROM TransactionCourant t WHERE t.compte.id = :compteId", TransactionCourant.class)
                    .setParameter("compteId", compteId)
                    .getResultList();
    }
}
