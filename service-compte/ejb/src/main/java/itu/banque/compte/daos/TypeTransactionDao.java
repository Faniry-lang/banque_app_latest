package itu.banque.compte.daos;

import itu.banque.compte.entities.TypeTransaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TypeTransactionDao {

    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    public TypeTransaction save(TypeTransaction type) {
        em.persist(type);
        return type;
    }

    public TypeTransaction findById(Integer id) {
        return em.find(TypeTransaction.class, id);
    }

    public List<TypeTransaction> findAll() {
        return em.createQuery("SELECT t FROM TypeTransaction t", TypeTransaction.class).getResultList();
    }

    public TypeTransaction update(TypeTransaction type) {
        return em.merge(type);
    }

    public void delete(TypeTransaction type) {
        em.remove(em.contains(type) ? type : em.merge(type));
    }
}
