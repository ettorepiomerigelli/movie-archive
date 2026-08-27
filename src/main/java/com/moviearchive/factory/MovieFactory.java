package com.moviearchive.factory;

import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;

import java.time.Year;
import java.util.UUID;

/**
 * Factory Method per la creazione di oggetti Movie.
 *
 * Centralizza in un unico punto sia la validazione dei dati in ingresso
 * sia la generazione dell'identificativo univoco, cosi' che nessuna parte
 * del sistema possa costruire un Movie in uno stato incoerente
 * (es. anno di uscita nel futuro, valutazione fuori scala).
 *
 * Espone due varianti:
 *  - createNewMovie(...): genera un nuovo id (nuovo inserimento da UI);
 *  - recreateMovie(...): riusa un id gia' esistente (ricostruzione da file
 *    durante il caricamento, dove l'id va preservato).
 */
public final class MovieFactory {

    private MovieFactory() {
        // Classe di utilita': non istanziabile
    }

    public static Movie createNewMovie(String title, String director, int releaseYear,
                                        Genre genre, int personalRating, ViewingStatus status) {
        return build(UUID.randomUUID().toString(), title, director, releaseYear,
                genre, personalRating, status);
    }

    public static Movie recreateMovie(String id, String title, String director, int releaseYear,
                                       Genre genre, int personalRating, ViewingStatus status) {
        return build(id, title, director, releaseYear, genre, personalRating, status);
    }

    private static Movie build(String id, String title, String director, int releaseYear,
                                Genre genre, int personalRating, ViewingStatus status) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Il titolo del film e' obbligatorio.");
        }
        if (director == null || director.isBlank()) {
            throw new IllegalArgumentException("Il regista del film e' obbligatorio.");
        }
        int currentYear = Year.now().getValue();
        if (releaseYear < 1888 || releaseYear > currentYear + 1) {
            throw new IllegalArgumentException(
                    "L'anno di uscita deve essere compreso tra il 1888 e il " + (currentYear + 1) + ".");
        }
        if (personalRating < 0 || personalRating > 5) {
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 0 e 5.");
        }
        if (status != ViewingStatus.WATCHED && personalRating != 0) {
            throw new IllegalArgumentException(
                    "La valutazione deve essere 0 se il film non è ancora stato visto.");
        }

        Genre safeGenre = (genre != null) ? genre : Genre.UNCLASSIFIED;
        ViewingStatus safeStatus = (status != null) ? status : ViewingStatus.PLANNED;

        return new Movie(id, title.trim(), director.trim(), releaseYear,
                safeGenre, personalRating, safeStatus);
    }
}
