package com.moviearchive.strategy;

import com.moviearchive.model.Movie;

public class SortByYear implements SortStrategy {

    @Override
    public int compare(Movie a, Movie b) {
        return Integer.compare(b.getReleaseYear(), a.getReleaseYear()); // piu' recenti prima
    }

    @Override
    public String getLabel() {
        return "Anno (piu' recente prima)";
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
