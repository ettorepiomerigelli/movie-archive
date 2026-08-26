package com.moviearchive.strategy;

import com.moviearchive.model.Movie;

import java.util.Comparator;

/**
 * Interfaccia Strategy per l'ordinamento dei film.
 * Estende Comparator<Movie> cosi' ogni strategia concreta si riduce a
 * definire compare(...), e puo' essere passata direttamente a
 * List.sort(...) o combinata con Comparator.thenComparing(...).
 */
public interface SortStrategy extends Comparator<Movie> {

    /**
     * Nome descrittivo mostrato nella combo di selezione dell'ordinamento.
     */
    String getLabel();
}
