package com.moviearchive.command;

import com.moviearchive.model.Movie;
import com.moviearchive.model.MovieArchive;

/**
 * Comando di modifica. Conserva sia lo stato precedente che quello nuovo
 * del film, cosi' execute()/undo() sono semplicemente due sostituzioni
 * simmetriche nell'archivio.
 */
public class UpdateMovieCommand implements Command {

    private final MovieArchive archive;
    private final Movie previousState;
    private final Movie newState;

    public UpdateMovieCommand(MovieArchive archive, Movie previousState, Movie newState) {
        this.archive = archive;
        this.previousState = previousState.copy();
        this.newState = newState;
    }

    @Override
    public void execute() {
        archive.update(newState);
    }

    @Override
    public void undo() {
        archive.update(previousState);
    }
}
