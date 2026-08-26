package com.moviearchive.filter;

import com.moviearchive.model.Movie;

/**
 * Filtro di ricerca testuale libera su titolo e regista.
 * Il confronto è case-insensitive e per sottostringa.
 */
public class TextSearchFilter implements MovieFilter {

    private final String query;

    public TextSearchFilter(String query) {
        this.query = query == null ? "" : query.trim().toLowerCase();
    }

    @Override
    public boolean matches(Movie movie) {
        if (query.isEmpty()) {
            return true;
        }
        return movie.getTitle().toLowerCase().contains(query)
                || movie.getDirector().toLowerCase().contains(query);
    }
}
