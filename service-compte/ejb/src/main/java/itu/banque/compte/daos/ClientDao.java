package itu.banque.compte.daos;

import itu.banque.compte.entities.Client;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ClientDao {

    @PersistenceContext(name = "service-comptePersistenceUnit")
    EntityManager em;

    public Client save(Client client) {
        em.persist(client);
        return client;
    }

    public Client findById(Integer id) {
        return em.find(Client.class, id);
    }

    public List<Client> findAll() {
        return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
    }

    public Client update(Client client) {
        return em.merge(client);
    }

    public void delete(Client client) {
        em.remove(em.contains(client) ? client : em.merge(client));
    }
}
