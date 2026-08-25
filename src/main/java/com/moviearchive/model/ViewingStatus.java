package com.moviearchive.model;

/**
 * Stato di visione di un film all'interno dell'archivio personale
 * dell'utente. Rispetto a un semplice flag booleano "visto/non visto",
 * uno stato a tre valori permette di rappresentare anche i film che
 * l'utente sta guardando in questo momento (es. serie o film in piu' parti).
 */
public enum ViewingStatus {

    WATCHED("Visto"),
    PLANNED("Da vedere"),
    IN_PROGRESS("In visione");

    private final String label;

    ViewingStatus(String label) {
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
