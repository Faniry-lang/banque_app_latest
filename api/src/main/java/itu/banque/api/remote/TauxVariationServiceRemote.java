package itu.banque.api.remote;

import itu.banque.api.dtos.TauxVariation;
import jakarta.ejb.Remote;

import java.time.LocalDate;
import java.util.List;

@Remote
public interface TauxVariationServiceRemote {
    List<TauxVariation> getAll();
    TauxVariation getById(Integer ref);
    void add(TauxVariation taux);
    void update(TauxVariation taux);
    void delete(Integer ref);
    TauxVariation getTauxVariationForDevise(String deviseName, LocalDate date);
}
