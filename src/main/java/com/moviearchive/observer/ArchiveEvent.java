package com.moviearchive.observer;

import com.moviearchive.model.Movie;

/**
 * Evento notificato agli observer quando l'archivio dei film cambia.
 *
 * Si è scelto un modello "push" (l'evento porta con se' il tipo di
 * modifica e il film coinvolto) invece di un modello "pull" in cui
 * l'observer deve ri-leggere l'intera collezione per capire cosa è
 * cambiato. Il modello push rende piu' esplicito il contratto fra
 * Subject e Observer ed è piu' facile da estendere in futuro
 * (es. per animare in UI solo la riga effettivamente modificata).
 */
public class ArchiveEvent {

    public enum Type { ADDED, REMOVED, UPDATED, RELOADED }

    private final Type type;
    private final Movie movie;

    public ArchiveEvent(Type type, Movie movie) {
        this.type = type;
        this.movie = movie;
    }

    public Type getType() {
        return type;
    }

    /**
     * Il film coinvolto nell'evento. E' null per RELOADED, che segnala
     * un cambiamento massivo (es. import) non riconducibile a un singolo film.
     */
    public Movie getMovie() {
        return movie;
    }
}
