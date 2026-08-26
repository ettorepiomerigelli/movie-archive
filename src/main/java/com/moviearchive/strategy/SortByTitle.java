package com.moviearchive.strategy;

import com.moviearchive.model.Movie;

public class SortByTitle implements SortStrategy {

    @Override
    public int compare(Movie a, Movie b) {
        return a.getTitle().compareToIgnoreCase(b.getTitle());
    }

    @Override
    public String getLabel() {
        return "Titolo (A-Z)";
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
