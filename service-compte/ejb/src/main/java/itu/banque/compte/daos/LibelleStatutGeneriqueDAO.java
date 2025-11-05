package itu.banque.compte.daos;

import itu.banque.compte.entities.LibelleStatutGenerique;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class LibelleStatutGeneriqueDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public LibelleStatutGenerique create(LibelleStatutGenerique libelleStatutGenerique) {
        em.persist(libelleStatutGenerique);
        return libelleStatutGenerique;
    }

    public LibelleStatutGenerique findById(Integer id) {
        return em.find(LibelleStatutGenerique.class, id);
    }

    public List<LibelleStatutGenerique> findAll() {
        return em.createQuery("SELECT e FROM LibelleStatutGenerique e", LibelleStatutGenerique.class).getResultList();
    }

    public LibelleStatutGenerique findByLibelleAndTableReference(String tableReference, String libelle) throws NoResultException {
        try {
            return em.createQuery("SELECT e FROM LibelleStatutGenerique e WHERE e.libelle = :libelle AND e.tableReference = :tableReference ", LibelleStatutGenerique.class)
                    .setParameter("libelle", libelle)
                    .setParameter("tableReference", tableReference)
                    .setMaxResults(1).getSingleResult();
        } catch(NoResultException e)
        {
            throw new NoResultException("Aucun libelle trouvé pour '"+libelle+"'");
        }
    }

    public LibelleStatutGenerique update(LibelleStatutGenerique libelleStatutGenerique) {
        return em.merge(libelleStatutGenerique);
    }

    public void delete(Integer id) {
        LibelleStatutGenerique libelleStatutGenerique = findById(id);
        if (libelleStatutGenerique != null) {
            em.remove(libelleStatutGenerique);
        }
    }
}
