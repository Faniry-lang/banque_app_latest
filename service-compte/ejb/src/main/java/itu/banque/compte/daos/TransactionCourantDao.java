package itu.banque.compte.daos;

import itu.banque.compte.entities.TransactionCourant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public List<TransactionCourant> update(List<TransactionCourant> transactions) {
        return transactions.stream()
                .map(em::merge)
                .collect(java.util.stream.Collectors.toList());
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

    public List<TransactionCourant> getAllByMonthAndCompteId(LocalDate date, Integer compteId, String libelle)
    {
        return em.createQuery("SELECT e FROM TransactionCourant e WHERE MONTH(e.dateTransaction) = :month AND YEAR(e.dateTransaction) = :year AND e.compte.id = :compteId AND e.contexteTransaction.libelle = :libelle", TransactionCourant.class)
                .setParameter("month", date.getMonthValue())
                .setParameter("year", date.getYear())
                .setParameter("compteId", compteId)
                .setParameter("libelle", libelle)
                .getResultList();
    }

    public List<TransactionCourant> getAllBeforeDate(LocalDate date, Integer compteId) {
        String jpql = "SELECT e FROM TransactionCourant e WHERE e.compte.id = :compteId";
        
        if (date != null) {
            jpql += " AND e.dateTransaction <= :date";
        }

        TypedQuery<TransactionCourant> query = em.createQuery(jpql, TransactionCourant.class)
                                                .setParameter("compteId", compteId);

        if (date != null) {
            query.setParameter("date", date);
        }

        return query.getResultList();
    }

    public List<TransactionCourant> getAllValideBeforeDate(LocalDate date, Integer compteId) 
    {
        String jpql = "SELECT t FROM TransactionCourant t WHERE t.compte.id = :compteId ";

        if (date != null) {
            jpql += "AND t.dateTransaction <= :date ";
        }

        jpql += "AND EXISTS (" +
                "  SELECT s FROM StatutGenerique s JOIN s.libelle l " +
                "  WHERE s.idReference = t.id " +
                "  AND s.tableReference = 'transaction_courant' " +
                "  AND l.libelle = 'VALIDE' " +
                "  AND s.dateStatut = (" +
                "    SELECT MAX(s2.dateStatut) FROM StatutGenerique s2 " +
                "    WHERE s2.idReference = t.id " +
                "    AND s2.tableReference = 'transaction_courant' ";

        if (date != null) {
            jpql += "    AND s2.dateStatut <= :date ";
        }

        jpql += "  )" + 
                ")";   

        TypedQuery<TransactionCourant> query = em.createQuery(jpql, TransactionCourant.class);
        query.setParameter("compteId", compteId);

        if (date != null) {
            query.setParameter("date", date);
        }

        return query.getResultList();
    }

    public List<TransactionCourant> getByDeviseRef(Integer deviseRef) {
        return em.createQuery("SELECT t FROM TransactionCourant t WHERE t.deviseRef = :deviseRef", TransactionCourant.class)
                .setParameter("deviseRef", deviseRef)
                .getResultList();
    }

}
