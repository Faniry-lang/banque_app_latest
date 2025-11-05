package itu.banque.compte.daos;

import itu.banque.api.dtos.FraisBancaireDto;
import itu.banque.compte.entities.FraisBancaire;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class FraisBancaireDAO {

    @PersistenceContext(unitName = "service-comptePersistenceUnit")
    private EntityManager em;

    public FraisBancaire create(FraisBancaire fraisBancaire) {
        em.persist(fraisBancaire);
        return fraisBancaire;
    }

    public FraisBancaire findById(Integer id) {
        return em.find(FraisBancaire.class, id);
    }

    public List<FraisBancaire> findAll() {
        return em.createQuery("SELECT e FROM FraisBancaire e", FraisBancaire.class).getResultList();
    }

    public FraisBancaire update(FraisBancaire fraisBancaire) {
        return em.merge(fraisBancaire);
    }

    public void delete(Integer id) {
        FraisBancaire fraisBancaire = findById(id);
        if (fraisBancaire != null) {
            em.remove(fraisBancaire);
        }
    }

    public FraisBancaire getByDateAndMontant(LocalDate date, BigDecimal montant) {
        try {
            return em.createQuery("SELECT e FROM FraisBancaire e WHERE e.dateFrais <= :date AND e.montantInf <= :montant AND e.montantSup >= :montant ORDER BY e.dateFrais DESC", FraisBancaire.class)
                    .setParameter("date", date)
                    .setParameter("montant", montant)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch(NoResultException e)
        {
            return null;
        }
    }
}
