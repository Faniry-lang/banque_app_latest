package itu.banque.change;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import itu.banque.api.dtos.TauxVariation;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TauxVariationFileManager {

    private static final String TAUX_JSON_PATH_PROPERTY = "taux-variation.json.path";
    private static final String DEFAULT_PATH = "/tmp/taux-variation.json";
    private final Path filePath;
    private final Gson gson;

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

    public TauxVariationFileManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        String pathStr = System.getProperty(TAUX_JSON_PATH_PROPERTY, DEFAULT_PATH);
        this.filePath = Paths.get(pathStr);

        if (!Files.exists(this.filePath)) {
            try {
                try (InputStream in = getClass().getResourceAsStream("/data/taux-variation.json");
                     OutputStream out = Files.newOutputStream(this.filePath)) {
                    if (in == null) {
                        Files.write(this.filePath, "[]".getBytes());
                    } else {
                        in.transferTo(out);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to initialize taux-variation.json", e);
            }
        }
    }

    public List<TauxVariation> lireTaux() throws IOException {
        try (Reader reader = Files.newBufferedReader(this.filePath)) {
            Type listType = new TypeToken<ArrayList<TauxVariation>>() {}.getType();
            List<TauxVariation> taux = gson.fromJson(reader, listType);
            return taux == null ? new ArrayList<>() : taux;
        }
    }

    public void ajouterTaux(TauxVariation taux) throws IOException {
        List<TauxVariation> allTaux = lireTaux();
        int maxRef = allTaux.stream().mapToInt(TauxVariation::getRef).max().orElse(0);
        taux.setRef(maxRef + 1);
        allTaux.add(taux);
        ecrireTaux(allTaux);
    }

    public void modifierTaux(TauxVariation tauxToUpdate) throws IOException {
        List<TauxVariation> allTaux = lireTaux();
        List<TauxVariation> updatedList = allTaux.stream()
                .map(t -> t.getRef().equals(tauxToUpdate.getRef()) ? tauxToUpdate : t)
                .collect(Collectors.toList());
        ecrireTaux(updatedList);
    }

    public void supprimerTaux(Integer ref) throws IOException {
        List<TauxVariation> allTaux = lireTaux();
        List<TauxVariation> updatedList = allTaux.stream()
                .filter(t -> !t.getRef().equals(ref))
                .collect(Collectors.toList());
        ecrireTaux(updatedList);
    }

    private void ecrireTaux(List<TauxVariation> taux) throws IOException {
        try (Writer writer = Files.newBufferedWriter(this.filePath)) {
            gson.toJson(taux, writer);
        }
    }
}
