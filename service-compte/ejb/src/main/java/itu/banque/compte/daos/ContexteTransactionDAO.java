package itu.banque.compte.daos;

import itu.banque.compte.entities.ContexteTransaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ContexteTransactionDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public ContexteTransaction create(ContexteTransaction contexteTransaction) {
        em.persist(contexteTransaction);
        return contexteTransaction;
    }

    public ContexteTransaction findById(Integer id) {
        return em.find(ContexteTransaction.class, id);
    }

    public List<ContexteTransaction> findAll() {
        return em.createQuery("SELECT e FROM ContexteTransaction e", ContexteTransaction.class).getResultList();
    }

    public ContexteTransaction findByLibelle(String libelle)
    {
        try {
            return em.createQuery("SELECT e FROM ContexteTransaction e WHERE e.libelle = :libelle", ContexteTransaction.class)
                        .setParameter("libelle", libelle)
                        .setMaxResults(1)
                        .getSingleResult();
        } catch(NoResultException e)
        {
            throw e;
        }
    }

    public ContexteTransaction update(ContexteTransaction contexteTransaction) {
        return em.merge(contexteTransaction);
    }

    public void delete(Integer id) {
        ContexteTransaction contexteTransaction = findById(id);
        if (contexteTransaction != null) {
            em.remove(contexteTransaction);
        }
    }
}
