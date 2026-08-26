package com.moviearchive.strategy;

import com.moviearchive.model.Movie;

public class SortByRating implements SortStrategy {

    @Override
    public int compare(Movie a, Movie b) {
        return Integer.compare(b.getPersonalRating(), a.getPersonalRating()); // voto piu' alto prima
    }

    @Override
    public String getLabel() {
        return "Valutazione (piu' alta prima)";
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
