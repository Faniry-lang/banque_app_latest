package itu.banque.central.daos;

import java.util.List;

import itu.banque.central.entities.Direction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class DirectionDao {
    
    @PersistenceContext(name = "service-centralPersistenceUnit")
    EntityManager em;

    public Direction save(Direction direction) {
        em.persist(direction);
        return direction;
    }

    public List<Direction> findAll() {
        return em.createQuery("SELECT d FROM Direction d", Direction.class)
                 .getResultList();
    }

    public Direction findById(Integer id) {
        return em.find(Direction.class, id);
    }

    public Direction update(Direction direction) {
        return em.merge(direction);
    }

    public void delete(Direction direction) {
        em.remove(em.contains(direction) ? direction : em.merge(direction));
    }

}
