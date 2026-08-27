package com.moviearchive.command;

import com.moviearchive.model.Movie;
import com.moviearchive.model.MovieArchive;

public class AddMovieCommand implements Command {

    private final MovieArchive archive;
    private final Movie movie;

    public AddMovieCommand(MovieArchive archive, Movie movie) {
        this.archive = archive;
        this.movie = movie;
    }

    @Override
    public void execute() {
        archive.add(movie);
    }

    @Override
    public void undo() {
        archive.remove(movie.getId());
    }
}
