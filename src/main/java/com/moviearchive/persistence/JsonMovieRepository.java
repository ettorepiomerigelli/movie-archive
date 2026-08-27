package com.moviearchive.persistence;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementazione di MovieRepository che persiste l'archivio in un unico
 * file JSON, come un array di oggetti con questa struttura:
 *
 * [
 *   {"id":"...", "title":"...", "director":"...", "releaseYear":2010,
 *    "genre":"SCI_FI", "personalRating":5, "status":"WATCHED"},
 *   ...
 * ]
 *
 * Si e' scelto di scrivere un piccolo (de)serializzatore dedicato invece
 * di introdurre una libreria esterna (es. Gson/Jackson): il formato dati
 * e' semplice e fisso, e questo evita una dipendenza aggiuntiva per un
 * progetto didattico di dimensioni contenute. Il parsing e' comunque
 * isolato in questa classe: se in futuro servisse una libreria dedicata,
 * il resto del sistema non cambierebbe (dipende solo da MovieRepository).
 */
public class JsonMovieRepository implements MovieRepository {

    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{[^{}]*}");
    private static final Pattern FIELD_PATTERN =
            Pattern.compile("\"(\\w+)\"\\s*:\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|-?\\d+)");

    private final Path filePath;

    public JsonMovieRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void saveAll(List<Movie> movies) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < movies.size(); i++) {
            json.append("  ").append(toJsonObject(movies.get(i)));
            if (i < movies.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            Files.writeString(filePath, json.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante il salvataggio dell'archivio su " + filePath, e);
        }
    }

    @Override
    public List<Movie> loadAll() {
        List<Movie> movies = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return movies;
        }

        String content;
        try {
            content = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante la lettura dell'archivio da " + filePath, e);
        }

        Matcher objectMatcher = OBJECT_PATTERN.matcher(content);
        while (objectMatcher.find()) {
            Movie movie = parseMovie(objectMatcher.group());
            if (movie != null) {
                movies.add(movie);
            }
        }
        return movies;
    }

    private String toJsonObject(Movie movie) {
        return "{"
                + "\"id\":\"" + escape(movie.getId()) + "\","
                + "\"title\":\"" + escape(movie.getTitle()) + "\","
                + "\"director\":\"" + escape(movie.getDirector()) + "\","
                + "\"releaseYear\":" + movie.getReleaseYear() + ","
                + "\"genre\":\"" + movie.getGenre().name() + "\","
                + "\"personalRating\":" + movie.getPersonalRating() + ","
                + "\"status\":\"" + movie.getStatus().name() + "\""
                + "}";
    }

    private Movie parseMovie(String jsonObject) {
        java.util.Map<String, String> fields = new java.util.HashMap<>();
        Matcher fieldMatcher = FIELD_PATTERN.matcher(jsonObject);
        while (fieldMatcher.find()) {
            String key = fieldMatcher.group(1);
            String rawValue = fieldMatcher.group(2);
            String value = rawValue.startsWith("\"")
                    ? unescape(rawValue.substring(1, rawValue.length() - 1))
                    : rawValue;
            fields.put(key, value);
        }

        try {
            return MovieFactory.recreateMovie(
                    fields.get("id"),
                    fields.get("title"),
                    fields.get("director"),
                    Integer.parseInt(fields.get("releaseYear")),
                    Genre.valueOf(fields.get("genre")),
                    Integer.parseInt(fields.get("personalRating")),
                    ViewingStatus.valueOf(fields.get("status"))
            );
        } catch (Exception e) {
            System.err.println("Voce JSON non valida, ignorata: " + jsonObject);
            return null;
        }
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
