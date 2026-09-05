package com.moviearchive.model;

import com.moviearchive.observer.ArchiveEvent;
import com.moviearchive.observer.ArchiveObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Archivio dei film dell'utente. Ricopre il ruolo di Subject nel pattern
 * Observer: ogni operazione che cambia lo stato della collezione produce
 * un ArchiveEvent inviato a tutti gli observer registrati (in genere il
 * Controller, che a sua volta aggiorna persistenza e View).
 *
 * La classe non conosce ne' la View ne' la persistenza: si limita a
 * mantenere coerente lo stato in memoria e a notificarne i cambiamenti.
 *
 * Copie difensive: Movie e' un oggetto mutabile (ha dei setter), quindi
 * senza precauzioni un chiamante esterno potrebbe ottenere un riferimento
 * a un Movie tramite getAll()/findById() e modificarlo direttamente con
 * un setter, bypassando completamente Command, Observer e persistenza.
 * Per questo ogni film che ENTRA nell'archivio (add, update, replaceAll)
 * viene copiato prima di essere memorizzato, ed ogni film che ESCE
 * dall'archivio (getAll, findById) e' anch'esso una copia: l'unico Movie
 * "vero" e' quello custodito nella lista interna, mai esposto all'esterno.
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
        Movie stored = movie.copy();
        movies.add(stored);
        notify(new ArchiveEvent(ArchiveEvent.Type.ADDED, stored));
    }

    public void remove(String movieId) {
        findInternal(movieId).ifPresent(movie -> {
            movies.remove(movie);
            notify(new ArchiveEvent(ArchiveEvent.Type.REMOVED, movie.copy()));
        });
    }

    public void update(Movie updatedMovie) {
        if (updatedMovie == null) {
            return;
        }
        int index = movies.indexOf(updatedMovie);
        if (index != -1) {
            Movie stored = updatedMovie.copy();
            movies.set(index, stored);
            notify(new ArchiveEvent(ArchiveEvent.Type.UPDATED, stored));
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
        for (Movie movie : newMovies) {
            movies.add(movie.copy());
        }
        notify(new ArchiveEvent(ArchiveEvent.Type.RELOADED, null));
    }

    public List<Movie> getAll() {
        return movies.stream().map(Movie::copy).collect(Collectors.toUnmodifiableList());
    }

    public Optional<Movie> findById(String id) {
        return findInternal(id).map(Movie::copy);
    }

    /**
     * Ricerca interna che restituisce il riferimento REALE (non una copia)
     * usato dai metodi della classe stessa (remove, containsDuplicate) per
     * operare sulla lista interna. Non e' esposto pubblicamente: solo
     * findById() lo e', e restituisce sempre una copia (vedi sopra).
     */
    private Optional<Movie> findInternal(String id) {
        return movies.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    /**
     * Verifica se esiste gia' nell'archivio un film "equivalente" a quello
     * candidato, cioe' con stesso titolo, regista e anno di uscita
     * (confronto case-insensitive sui testi). L'id del film candidato
     * viene sempre escluso dal confronto: questo permette di riusare lo
     * stesso controllo sia in fase di aggiunta (dove non esiste ancora
     * nell'archivio) sia in fase di modifica (dove il film esiste gia'
     * con quello stesso id, e non deve risultare "duplicato di se stesso").
     */
    public boolean containsDuplicate(Movie candidate) {
        return movies.stream().anyMatch(existing ->
                !existing.getId().equals(candidate.getId())
                        && existing.getTitle().equalsIgnoreCase(candidate.getTitle())
                        && existing.getDirector().equalsIgnoreCase(candidate.getDirector())
                        && existing.getReleaseYear() == candidate.getReleaseYear());
    }

    public int size() {
        return movies.size();
    }
}