package com.moviearchive.filter;

import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;

public class GenreFilter implements MovieFilter {

    private final Genre genre;

    public GenreFilter(Genre genre) {
        this.genre = genre;
    }

    @Override
    public boolean matches(Movie movie) {
        return movie.getGenre() == genre;
    }
}
