package itu.banque.central.daos;

import java.util.List;

import itu.banque.central.entities.ActionRole;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ActionRoleDao {
    
    @PersistenceContext(name = "service-centralPersistenceUnit")
    EntityManager em;

    public ActionRole save(ActionRole actionRole) {
        em.persist(actionRole);
        return actionRole;
    }

    public List<ActionRole> findAll() {
        return em.createQuery("SELECT ar FROM ActionRole ar", ActionRole.class)
                 .getResultList();
    }

    public List<ActionRole> findAllowedRoles(Integer roleLvl) {
        return em.createQuery("SELECT ar FROM ActionRole ar WHERE ar.roleLvl <= :roleLvl", ActionRole.class)
                 .setParameter("roleLvl", roleLvl)
                 .getResultList();
    }

    public ActionRole findById(Integer id) {
        return em.find(ActionRole.class, id);
    }

    public ActionRole update(ActionRole actionRole) {
        return em.merge(actionRole);
    }

    public void delete(ActionRole actionRole) {
        em.remove(em.contains(actionRole) ? actionRole : em.merge(actionRole));
    }

}
