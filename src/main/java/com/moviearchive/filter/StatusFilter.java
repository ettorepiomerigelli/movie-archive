package com.moviearchive.filter;

import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;

public class StatusFilter implements MovieFilter {

    private final ViewingStatus status;

    public StatusFilter(ViewingStatus status) {
        this.status = status;
    }

    @Override
    public boolean matches(Movie movie) {
        return movie.getStatus() == status;
    }
}
