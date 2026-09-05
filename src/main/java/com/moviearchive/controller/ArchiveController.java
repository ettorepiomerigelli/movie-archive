package com.moviearchive.controller;

import com.moviearchive.command.AddMovieCommand;
import com.moviearchive.command.CommandManager;
import com.moviearchive.command.RemoveMovieCommand;
import com.moviearchive.command.UpdateMovieCommand;
import com.moviearchive.filter.CompositeMovieFilter;
import com.moviearchive.filter.GenreFilter;
import com.moviearchive.filter.MovieFilter;
import com.moviearchive.filter.StatusFilter;
import com.moviearchive.filter.TextSearchFilter;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.MovieArchive;
import com.moviearchive.model.DuplicateMovieException;
import com.moviearchive.model.ViewingStatus;
import com.moviearchive.observer.ArchiveEvent;
import com.moviearchive.observer.ArchiveObserver;
import com.moviearchive.persistence.MovieRepository;
import com.moviearchive.strategy.SortByTitle;
import com.moviearchive.strategy.SortStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller dell'applicazione (pattern MVC).
 *
 * Coordina il MovieArchive (model), il MovieRepository (persistenza) e
 * il CommandManager (undo/redo); si registra come ArchiveObserver per
 * salvare automaticamente su disco e richiedere alla View di aggiornarsi
 * ogni volta che l'archivio cambia stato.
 *
 * La View non viene mai passata al costruttore per evitare la dipendenza
 * circolare Controller<->View: viene collegata in un secondo momento con
 * setViewRefreshCallback, chiamato da Main dopo che la View e' stata creata.
 */
public class ArchiveController implements ArchiveObserver {

    private final MovieArchive archive;
    private final MovieRepository repository;
    private final CommandManager commandManager;

    private SortStrategy sortStrategy;
    private Genre activeGenreFilter;
    private ViewingStatus activeStatusFilter;
    private String activeSearchQuery = "";

    private Runnable viewRefreshCallback;

    //Senza questa guardia, un file corrotto verrebbe
    // sovrascritto con un archivio vuoto ancora prima che l'utente veda
    // la finestra principale.
    private boolean initializing = true;

    public ArchiveController(MovieRepository repository) {
        this.archive = new MovieArchive();
        this.repository = repository;
        this.commandManager = new CommandManager();
        this.sortStrategy = new SortByTitle();

        archive.addObserver(this);
        archive.replaceAll(repository.loadAll());
        initializing = false;
    }

    public void setViewRefreshCallback(Runnable callback) {
        this.viewRefreshCallback = callback;
    }

    // --- ArchiveObserver ---------------------------------------------

    @Override
    public void onArchiveChanged(ArchiveEvent event) {
        if (!initializing) {
            repository.saveAll(archive.getAll());
        }
        if (viewRefreshCallback != null) {
            viewRefreshCallback.run();
        }
    }

    // --- Operazioni CRUD (tramite Command, per abilitare undo/redo) ---

    public void addMovie(Movie movie) {
        if (archive.containsDuplicate(movie)) {
            throw new DuplicateMovieException(
                    "Esiste gia' un film con lo stesso titolo, regista e anno: \""
                            + movie.getTitle() + "\" (" + movie.getReleaseYear() + ").");
        }
        commandManager.executeCommand(new AddMovieCommand(archive, movie));
    }

    public void removeMovie(String movieId) {
        Optional<Movie> toRemove = archive.findById(movieId);
        toRemove.ifPresent(movie ->
                commandManager.executeCommand(new RemoveMovieCommand(archive, movie)));
    }

    public void updateMovie(Movie updatedMovie) {
        if (archive.containsDuplicate(updatedMovie)) {
            throw new DuplicateMovieException(
                    "Esiste gia' un altro film con lo stesso titolo, regista e anno: \""
                            + updatedMovie.getTitle() + "\" (" + updatedMovie.getReleaseYear() + ").");
        }
        Optional<Movie> previous = archive.findById(updatedMovie.getId());
        previous.ifPresent(oldState ->
                commandManager.executeCommand(new UpdateMovieCommand(archive, oldState, updatedMovie)));
    }

    public Optional<Movie> getMovieById(String id) {
        return archive.findById(id);
    }

    public int getTotalMovies() {
        return archive.size();
    }

    // --- Undo / Redo ----------------------------------------------------

    public void undo() {
        commandManager.undo();
    }

    public void redo() {
        commandManager.redo();
    }

    public boolean canUndo() {
        return commandManager.canUndo();
    }

    public boolean canRedo() {
        return commandManager.canRedo();
    }

    // --- Ricerca, filtro, ordinamento ------------------------------------

    public void setSearchQuery(String query) {
        this.activeSearchQuery = (query == null) ? "" : query;
        refreshView();
    }

    public void setGenreFilter(Genre genre) {
        this.activeGenreFilter = genre;
        refreshView();
    }

    public void setStatusFilter(ViewingStatus status) {
        this.activeStatusFilter = status;
        refreshView();
    }

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
        refreshView();
    }

    private void refreshView() {
        if (viewRefreshCallback != null) {
            viewRefreshCallback.run();
        }
    }

    /**
     * Calcola la lista di film da mostrare, applicando in sequenza il
     * filtro composito (genere + stato + ricerca testuale, tutti
     * opzionali) e l'ordinamento corrente.
     */
    public List<Movie> getVisibleMovies() {
        MovieFilter filter = buildActiveFilter();

        List<Movie> visible = new ArrayList<>();
        for (Movie movie : archive.getAll()) {
            if (filter.matches(movie)) {
                visible.add(movie);
            }
        }
        visible.sort(sortStrategy);
        return visible;
    }

    private MovieFilter buildActiveFilter() {
        CompositeMovieFilter composite = new CompositeMovieFilter();
        if (activeGenreFilter != null) {
            composite.add(new GenreFilter(activeGenreFilter));
        }
        if (activeStatusFilter != null) {
            composite.add(new StatusFilter(activeStatusFilter));
        }
        if (activeSearchQuery != null && !activeSearchQuery.isBlank()) {
            composite.add(new TextSearchFilter(activeSearchQuery));
        }
        return composite;
    }
}