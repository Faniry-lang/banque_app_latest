package itu.banque.change;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import itu.banque.api.dtos.Devise;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeviseFileManager {

    private static final String DEVISES_JSON_PATH_PROPERTY = "devises.json.path";
    private static final String DEFAULT_PATH = "/tmp/devises.json";
    private final Path filePath;
    private final Gson gson;

    // Adapter for LocalDate
    public static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.toString());
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString());
        }
    }

    public DeviseFileManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        String pathStr = System.getProperty(DEVISES_JSON_PATH_PROPERTY, DEFAULT_PATH);
        this.filePath = Paths.get(pathStr);
        // Ensure the file exists when the manager is initialized
        if (!Files.exists(this.filePath)) {
            try {
                // If the file doesn't exist, try to create it with initial data from classpath
                try (InputStream in = getClass().getResourceAsStream("/data/devises.json");
                     OutputStream out = Files.newOutputStream(this.filePath)) {
                    if (in == null) {
                        // If not in classpath, create an empty file
                        Files.write(this.filePath, "[]".getBytes());
                    } else {
                        in.transferTo(out);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to initialize devises.json", e);
            }
        }
    }

    public List<Devise> lireDevises() throws IOException {
        try (Reader reader = Files.newBufferedReader(this.filePath)) {
            Type listType = new TypeToken<ArrayList<Devise>>() {}.getType();
            List<Devise> devises = gson.fromJson(reader, listType);
            return devises == null ? new ArrayList<>() : devises;
        }
    }

    public void ajouterDevise(Devise devise) throws IOException {
        List<Devise> devises = lireDevises();
        Devise lastDevise = null;
        Integer ref = 1;
        try {
            lastDevise = devises.get(devises.size()-1);
            ref = lastDevise.getRef() + 1;
        } catch(IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }

        devise.setRef(ref);
        devises.add(devise);
        ecrireDevises(devises);
    }

    public void modifierDevise(Devise deviseToUpdate) throws IOException {
        List<Devise> devises = lireDevises();
        List<Devise> updatedList = devises.stream()
                .map(devise -> devise.getRef().equals(deviseToUpdate.getRef()) ? deviseToUpdate : devise)
                .collect(Collectors.toList());
        ecrireDevises(updatedList);
    }

    public void supprimerDevise(Integer ref) throws IOException {
        List<Devise> devises = lireDevises();
        List<Devise> updatedList = devises.stream()
                .filter(devise -> !devise.getRef().equals(ref))
                .collect(Collectors.toList());
        ecrireDevises(updatedList);
    }

    private void ecrireDevises(List<Devise> devises) throws IOException {
        try (Writer writer = Files.newBufferedWriter(this.filePath)) {
            gson.toJson(devises, writer);
        }
    }
}