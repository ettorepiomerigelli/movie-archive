package com.moviearchive.model;

/**
 * Generi cinematografici disponibili per classificare un film in archivio.
 * Ogni valore porta con se' un'etichetta leggibile da mostrare nella UI,
 * cosi' da non legare la lingua dell'interfaccia al nome della costante Java.
 */
public enum Genre {

    ACTION("Azione"),
    COMEDY("Commedia"),
    DRAMA("Drammatico"),
    HORROR("Horror"),
    SCI_FI("Fantascienza"),
    THRILLER("Thriller"),
    ANIMATION("Animazione"),
    DOCUMENTARY("Documentario"),
    ROMANCE("Romantico"),
    FANTASY("Fantasy"),
    UNCLASSIFIED("Non classificato");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
