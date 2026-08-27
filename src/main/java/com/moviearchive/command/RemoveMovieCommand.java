package com.moviearchive.command;

import com.moviearchive.model.Movie;
import com.moviearchive.model.MovieArchive;

/**
 * Comando di rimozione. Conserva una copia del film rimosso in modo da
 * poterlo reinserire esattamente com'era in caso di undo.
 */
public class RemoveMovieCommand implements Command {

    private final MovieArchive archive;
    private final Movie removedMovieSnapshot;

    public RemoveMovieCommand(MovieArchive archive, Movie movieToRemove) {
        this.archive = archive;
        this.removedMovieSnapshot = movieToRemove.copy();
    }

    @Override
    public void execute() {
        archive.remove(removedMovieSnapshot.getId());
    }

    @Override
    public void undo() {
        archive.add(removedMovieSnapshot);
    }
}
