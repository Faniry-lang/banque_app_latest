package itu.banque.compte.daos;

import itu.banque.compte.entities.Plafond;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

@Stateless
public class PlafondDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public Plafond create(Plafond plafond) {
        em.persist(plafond);
        return plafond;
    }

    public Plafond findById(Integer id) {
        return em.find(Plafond.class, id);
    }

    public List<Plafond> findAll() {
        return em.createQuery("SELECT e FROM Plafond e", Plafond.class).getResultList();
    }

    public Plafond findApplicablePlafond(
            Integer idTypeTransaction,
            Integer idFrequencePlafond,
            Integer idContexteTransaction,
            Integer idCompte,
            LocalDate date
    ) {
        StringBuilder jpql = new StringBuilder(
            "SELECT p FROM Plafond p " +
            "WHERE p.typeTransaction.id = :idType " +
            "AND p.frequencePlafond.id = :idFreq " +
            "AND p.contexteTransaction.id = :idCtx "
        );

        if (idCompte != null) {
            jpql.append("AND (p.compte.id = :idCompte OR p.compte IS NULL) ");
        }

        if (date != null) {
            jpql.append("AND p.dateDebut <= :date AND (p.dateFin IS NULL OR p.dateFin >= :date) ");
        }

        jpql.append("ORDER BY p.dateDebut DESC");

        TypedQuery<Plafond> query = em.createQuery(jpql.toString(), Plafond.class)
                                    .setParameter("idType", idTypeTransaction)
                                    .setParameter("idFreq", idFrequencePlafond)
                                    .setParameter("idCtx", idContexteTransaction);

        if (idCompte != null) {
            query.setParameter("idCompte", idCompte);
        }
        if (date != null) {
            query.setParameter("date", date);
        }

        List<Plafond> results = query.setMaxResults(1).getResultList();
        return results.isEmpty() ? null : results.get(0);
    }


    public Plafond update(Plafond plafond) {
        return em.merge(plafond);
    }

    public void delete(Integer id) {
        Plafond plafond = findById(id);
        if (plafond != null) {
            em.remove(plafond);
        }
    }
}
