package itu.banque.compte.daos;

import itu.banque.compte.entities.StatutGenerique;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class StatutGeneriqueDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public StatutGenerique create(StatutGenerique statutGenerique) {
        em.persist(statutGenerique);
        return statutGenerique;
    }

    public StatutGenerique findById(Integer id) {
        return em.find(StatutGenerique.class, id);
    }

    public List<StatutGenerique> findAll() {
        return em.createQuery("SELECT e FROM StatutGenerique e", StatutGenerique.class).getResultList();
    }

    public StatutGenerique findLatestByIdReference(String tableReference, Integer idReference) {
        try {
            return em.createQuery("SELECT e FROM StatutGenerique e WHERE e.tableReference = :tableReference AND e.idReference = :idReference ORDER BY e.dateStatut DESC, e.id DESC", StatutGenerique.class)
                        .setParameter("idReference", idReference)
                        .setParameter("tableReference", tableReference)
                        .setMaxResults(1)
                        .getSingleResult();
        } catch(NoResultException e)
        {
            return null;
        }
    }

    public StatutGenerique update(StatutGenerique statutGenerique) {
        return em.merge(statutGenerique);
    }

    public void delete(Integer id) {
        StatutGenerique statutGenerique = findById(id);
        if (statutGenerique != null) {
            em.remove(statutGenerique);
        }
    }

    public List<StatutGenerique> getLastStatutsByTableReference(String tableReference) {
        // This query uses a correlated subquery with NOT EXISTS to find the single
        // latest status for each idReference.
        // It selects a status 's' only if there does not exist another status 's2'
        // for the same idReference that is strictly newer (either by a later date,
        // or by a larger ID if the dates are the same).
        String jpql = "SELECT s FROM StatutGenerique s " +
                      "WHERE s.tableReference = :tableReference " +
                      "AND NOT EXISTS (" +
                      "  SELECT s2 FROM StatutGenerique s2 " +
                      "  WHERE s2.idReference = s.idReference " +
                      "  AND s2.tableReference = s.tableReference " +
                      "  AND (s2.dateStatut > s.dateStatut OR (s2.dateStatut = s.dateStatut AND s2.id > s.id))" +
                      ")";

        return em.createQuery(jpql, StatutGenerique.class)
                 .setParameter("tableReference", tableReference)
                 .getResultList();
    }

    public List<StatutGenerique> getLastStatutsForIdReferences(String tableReference, List<Integer> idReferences) {
        // Si la liste d'IDs est nulle ou vide, il n'y a rien à chercher.
        if (idReferences == null || idReferences.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String jpql = "SELECT s FROM StatutGenerique s " +
                      "WHERE s.tableReference = :tableReference " +
                      "AND s.idReference IN :idReferences " + // Filtre par la liste d'IDs fournie
                      "AND NOT EXISTS (" +
                      "  SELECT s2 FROM StatutGenerique s2 " +
                      "  WHERE s2.idReference = s.idReference " +
                      "  AND s2.tableReference = s.tableReference " +
                      "  AND (s2.dateStatut > s.dateStatut OR (s2.dateStatut = s.dateStatut AND s2.id > s.id))" +
                      ")";

        return em.createQuery(jpql, StatutGenerique.class)
                 .setParameter("tableReference", tableReference)
                 .setParameter("idReferences", idReferences) // Définit le paramètre de la liste
                 .getResultList();
    }
}
