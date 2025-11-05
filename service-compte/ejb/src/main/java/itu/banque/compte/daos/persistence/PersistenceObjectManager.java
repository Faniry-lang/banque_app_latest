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
import jakarta.persistence.criteria.Path;

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

        List<Predicate> andPredicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        for (Criterion criterion : criteria) {
            String field = criterion.fieldName();
            Object value = criterion.value();
            Operator op = criterion.operator();

            // Correction: Gérer les chemins imbriqués (ex: "compte.id")
            Path<?> path = root;
            String[] pathParts = field.split("\\.");
            for (String part : pathParts) {
                path = path.get(part);
            }

            Predicate p = switch (op) {
                case EQUALS -> cb.equal(path, value);
                case NOT_EQUALS -> cb.notEqual(path, value);
                case GREATER_THAN -> cb.greaterThan((Path<Comparable>) path, (Comparable) value);
                case LESS_THAN -> cb.lessThan((Path<Comparable>) path, (Comparable) value);
                case GREATER_OR_EQUALS -> cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
                case LESS_OR_EQUALS -> cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
                case LIKE -> cb.like((Path<String>) path, value.toString());
                case IN -> {
                    if (value instanceof Collection<?> col) {
                        yield path.in(col);
                    } else {
                        throw new IllegalArgumentException("IN operator requires a Collection value");
                    }
                }
                default -> throw new IllegalArgumentException("Operator not supported: " + op);
            };

            if (criterion.or()) {
                orPredicates.add(p);
            } else {
                andPredicates.add(p);
            }
        }

        // Combiner les OR
        Predicate orCombined = orPredicates.isEmpty() ? null : cb.or(orPredicates.toArray(new Predicate[0]));
        // Combiner les AND
        Predicate andCombined = andPredicates.isEmpty() ? null : cb.and(andPredicates.toArray(new Predicate[0]));

        // Combiner AND et OR ensemble
        Predicate finalPredicate;
        if (orCombined != null && andCombined != null) {
            finalPredicate = cb.and(andCombined, orCombined);
        } else if (orCombined != null) {
            finalPredicate = orCombined;
        } else {
            finalPredicate = andCombined;
        }

        if (finalPredicate != null) {
            cq.where(finalPredicate);
        }

        return em.createQuery(cq).getResultList();
    }

    public <T> T findOneByCriteria(Class<T> clazz, List<Criterion> criteria) {
        List<T> results = findByCriteria(clazz, criteria);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IllegalStateException("La requête a retourné plus d'un résultat. Attendu: 1, Obtenu: " + results.size());
        }
        return results.get(0);
    }

}
