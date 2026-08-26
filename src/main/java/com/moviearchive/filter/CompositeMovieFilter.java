package com.moviearchive.filter;

import com.moviearchive.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite: combina piu' MovieFilter in AND logico. Un film supera il
 * filtro composito solo se soddisfa tutti i criteri attualmente attivi.
 * Se non è stato aggiunto alcun filtro, si comporta come filtro
 * "neutro" (tutti i film passano), che è esattamente il comportamento
 * desiderato quando l'utente non ha impostato ne' ricerca ne' filtri.
 */
public class CompositeMovieFilter implements MovieFilter {

    private final List<MovieFilter> filters = new ArrayList<>();

    public CompositeMovieFilter add(MovieFilter filter) {
        if (filter != null) {
            filters.add(filter);
        }
        return this;
    }

    @Override
    public boolean matches(Movie movie) {
        for (MovieFilter filter : filters) {
            if (!filter.matches(movie)) {
                return false;
            }
        }
        return true;
    }
}
