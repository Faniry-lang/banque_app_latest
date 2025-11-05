package itu.banque.compte.daos.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import itu.banque.compte.daos.persistence.criteria.Criterion;
import itu.banque.compte.daos.persistence.criteria.Operator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PersistenceObjectManager {

    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    @Transactional
    public <T> T save(T entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    public <T> T update(T entity) {
        return em.merge(entity);
    }

    @Transactional
    public <T> void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public <T, ID> T findById(ID id, Class<T> clazz) {
        return em.find(clazz, id);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> List<T> findByCriteria(Class<T> clazz, List<Criterion> criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);

        List<Predicate> predicates = new ArrayList<>();

        for (Criterion criterion : criteria) {
            String field = criterion.fieldName();
            Object value = criterion.value();
            Operator op = criterion.operator();

            Predicate p = switch (op) {
                case EQUALS -> cb.equal(root.get(field), value);

                case NOT_EQUALS -> cb.notEqual(root.get(field), value);

                case GREATER_THAN -> cb.greaterThan(
                        root.get(field), 
                        (Comparable) value
                );

                case LESS_THAN -> cb.lessThan(
                        root.get(field),
                        (Comparable) value
                );

                case GREATER_OR_EQUALS -> cb.greaterThanOrEqualTo(
                        root.get(field),
                        (Comparable) value
                );

                case LESS_OR_EQUALS -> cb.lessThanOrEqualTo(
                        root.get(field),
                        (Comparable) value
                );

                case LIKE -> cb.like(
                        root.get(field),
                        value.toString()
                );

                case IN -> {
                    if (value instanceof Collection<?> col) {
                        yield root.get(field).in(col);
                    } else {
                        throw new IllegalArgumentException("IN operator requires a Collection value");
                    }
                }

                default -> throw new IllegalArgumentException("Operator not supported: " + op);
            };

            predicates.add(p);
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
