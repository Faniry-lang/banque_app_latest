package itu.banque.change.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import itu.banque.api.dtos.Devise;
import itu.banque.api.remote.DeviseServiceRemote;
import itu.banque.change.DeviseFileManager;
import jakarta.ejb.Stateless;

@Stateless
public class DeviseService implements DeviseServiceRemote {

    private static final String DEVISE_FILE_PATH = "/data/devises.csv";

    private List<Devise> getDevisesFromFile() {
        try (InputStream inputStream = DeviseService.class.getResourceAsStream(DEVISE_FILE_PATH)) {
            if (inputStream == null) {
                System.err.println("Cannot find the devise file: " + DEVISE_FILE_PATH);
                return new ArrayList<>();
            }
            DeviseFileManager fileManager = new DeviseFileManager();
            return fileManager.lireDevises(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Devise> getAll() {
        return getDevisesFromFile();
    }

    @Override
    public Devise getByNomEtDate(String nom, LocalDate date) {
        List<Devise> allDevises = getDevisesFromFile();

        for (Devise devise : allDevises) {
            if (devise.getNom().equalsIgnoreCase(nom)) {
                boolean isAfterOrEqualDebut = !date.isBefore(devise.getDateDebut());
                boolean isBeforeOrEqualFin = devise.getDateFin() == null || !date.isAfter(devise.getDateFin());
                if (isAfterOrEqualDebut && isBeforeOrEqualFin) {
                    return devise;
                }
            }
        }
        return null; // Not found
    }
}

