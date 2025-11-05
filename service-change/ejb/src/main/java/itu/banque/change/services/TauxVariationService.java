package itu.banque.change.services;

import itu.banque.api.dtos.TauxVariation;
import itu.banque.api.remote.TauxVariationServiceRemote;
import itu.banque.change.TauxVariationFileManager;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TauxVariationService implements TauxVariationServiceRemote {

    private List<TauxVariation> tauxVariations;
    private final TauxVariationFileManager fileManager = new TauxVariationFileManager();

    @PostConstruct
    public void init() {
        try {
            tauxVariations = fileManager.lireTaux();
        } catch (IOException e) {
            e.printStackTrace();
            tauxVariations = new ArrayList<>();
        }
    }

    @Override
    public List<TauxVariation> getAll() {
        return this.tauxVariations;
    }

    @Override
    public TauxVariation getById(Integer ref) {
        return this.tauxVariations.stream()
                .filter(t -> t.getRef().equals(ref))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void add(TauxVariation taux) {
        try {
            fileManager.ajouterTaux(taux);
            init(); // Reload list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(TauxVariation taux) {
        try {
            fileManager.modifierTaux(taux);
            init(); // Reload list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer ref) {
        try {
            fileManager.supprimerTaux(ref);
            init(); // Reload list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TauxVariation getTauxVariationForDevise(String deviseName, LocalDate date) {
        return tauxVariations.stream()
                .filter(t -> t.getDeviseName().equalsIgnoreCase(deviseName))
                .filter(t -> !date.isBefore(t.getDateDebut())) // date >= dateDebut
                .filter(t -> t.getDateFin() == null || !date.isAfter(t.getDateFin())) // date <= dateFin or dateFin is null
                .findFirst()
                .orElse(null);
    }
}
