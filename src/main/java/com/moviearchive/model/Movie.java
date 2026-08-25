package com.moviearchive.model;

import java.util.Objects;

/**
 * Rappresenta un film all'interno dell'archivio personale.
 *
 * A differenza di un approccio Builder, qui si è scelto un costruttore
 * a pacchetto piu' snello: Movie ha solo tre campi realmente opzionali
 * (genere, valutazione, stato) che vengono comunque sempre valorizzati
 * (con dei default) da chi crea l'oggetto. Non essendoci combinazioni
 * complesse di parametri opzionali da gestire, un Builder introdurrebbe
 * complessità non giustificata: la creazione è invece delegata a
 * {@link com.moviearchive.factory.MovieFactory}, che si occupa anche
 * della validazione dei dati in ingresso (pattern Factory Method).
 */
public class Movie {

    private final String id;
    private String title;
    private String director;
    private int releaseYear;
    private Genre genre;
    private int personalRating;   // 0-5, 0 = non valutato
    private ViewingStatus status;

    /**
     * Costruttore pubblico per necessità tecniche (deve essere
     * richiamabile da MovieFactory, che vive in un package separato),
     * ma per convenzione le istanze di Movie vanno create esclusivamente
     * tramite {@link com.moviearchive.factory.MovieFactory}, che si
     * occupa di validare i dati prima ancora che l'oggetto venga creato.
     * Nessun'altra classe del progetto invoca questo costruttore
     * direttamente.
     */
    public Movie(String id, String title, String director, int releaseYear,
                 Genre genre, int personalRating, ViewingStatus status) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.personalRating = personalRating;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(int personalRating) {
        this.personalRating = personalRating;
    }

    public ViewingStatus getStatus() {
        return status;
    }

    public void setStatus(ViewingStatus status) {
        this.status = status;
    }

    /**
     * Crea una copia indipendente del film corrente. Usata dal pattern
     * Command per salvare lo stato "prima della modifica" e poter cosi'
     * implementare l'undo di una UpdateMovieCommand.
     */
    public Movie copy() {
        return new Movie(id, title, director, releaseYear, genre, personalRating, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", releaseYear=" + releaseYear +
                ", genre=" + genre +
                ", personalRating=" + personalRating +
                ", status=" + status +
                '}';
    }
}
