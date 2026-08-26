package com.moviearchive.model;

import com.moviearchive.observer.ArchiveEvent;
import com.moviearchive.observer.ArchiveObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Archivio dei film dell'utente. Ricopre il ruolo di Subject nel pattern
 * Observer: ogni operazione che cambia lo stato della collezione produce
 * un ArchiveEvent inviato a tutti gli observer registrati (in genere il
 * Controller, che a sua volta aggiorna persistenza e View).
 *
 * La classe non conosce ne' la View ne' la persistenza: si limita a
 * mantenere coerente lo stato in memoria e a notificarne i cambiamenti.
 */
public class MovieArchive {

    private final List<Movie> movies = new ArrayList<>();
    private final List<ArchiveObserver> observers = new ArrayList<>();

    public void addObserver(ArchiveObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ArchiveObserver observer) {
        observers.remove(observer);
    }

    private void notify(ArchiveEvent event) {
        for (ArchiveObserver observer : observers) {
            observer.onArchiveChanged(event);
        }
    }

    public void add(Movie movie) {
        if (movie == null || movies.contains(movie)) {
            return;
        }
        movies.add(movie);
        notify(new ArchiveEvent(ArchiveEvent.Type.ADDED, movie));
    }

    public void remove(String movieId) {
        findById(movieId).ifPresent(movie -> {
            movies.remove(movie);
            notify(new ArchiveEvent(ArchiveEvent.Type.REMOVED, movie));
        });
    }

    public void update(Movie updatedMovie) {
        if (updatedMovie == null) {
            return;
        }
        int index = movies.indexOf(updatedMovie);
        if (index != -1) {
            movies.set(index, updatedMovie);
            notify(new ArchiveEvent(ArchiveEvent.Type.UPDATED, updatedMovie));
        }
    }

    /**
     * Sostituisce l'intero contenuto dell'archivio (usato al caricamento
     * iniziale da file). Genera un unico evento RELOADED invece di N
     * eventi ADDED, per evitare che la View si aggiorni inutilmente
     * una volta per ciascun film caricato.
     */
    public void replaceAll(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notify(new ArchiveEvent(ArchiveEvent.Type.RELOADED, null));
    }

    public List<Movie> getAll() {
        return Collections.unmodifiableList(movies);
    }

    public Optional<Movie> findById(String id) {
        return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public int size() {
        return movies.size();
    }
}
