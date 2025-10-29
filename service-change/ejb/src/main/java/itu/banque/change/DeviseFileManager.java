package itu.banque.change;

import itu.banque.api.dtos.Devise;
import itu.banque.api.dtos.Devise;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeviseFileManager {

    public List<Devise> lireDevises(InputStream inputStream) {
        List<Devise> devises = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 4) {
                    String nom = parts[0].trim();
                    LocalDate dateDebut = LocalDate.parse(parts[1].trim());
                    LocalDate dateFin = parts[2].trim().equals("null") ? null : LocalDate.parse(parts[2].trim());
                    double montant = Double.parseDouble(parts[3].trim());

                    devises.add(new Devise(nom, dateDebut, dateFin, montant));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return devises;
    }
}
