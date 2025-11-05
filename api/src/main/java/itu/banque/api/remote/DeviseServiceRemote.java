package itu.banque.api.remote;

import java.time.LocalDate;
import java.util.List;

import itu.banque.api.dtos.Devise;
import jakarta.ejb.Remote;

@Remote
public interface DeviseServiceRemote {
    List<Devise> getAll();
    Devise getByNomEtDate(String nom, LocalDate date);
    Devise getByRef(Integer ref);
    void add(Devise devise);
    void update(Devise devise);
    void delete(Integer ref);
    Devise getDevisePrecedente(Integer ref);
}
