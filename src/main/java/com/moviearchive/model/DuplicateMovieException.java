package com.moviearchive.model;

/**
 * Segnala il tentativo di aggiungere o modificare un film in modo che
 * risulti equivalente (stesso titolo, regista e anno) a un altro film
 * gia' presente nell'archivio. E' un'eccezione unchecked, sullo stesso
 * stile delle IllegalArgumentException lanciate da MovieFactory: entrambe
 * rappresentano un rifiuto di un'operazione per violazione di una regola
 * di validita' dei dati, solo a livello diverso (il singolo oggetto Movie
 * per MovieFactory, l'intera collezione per MovieArchive).
 */
public class DuplicateMovieException extends RuntimeException {

    public DuplicateMovieException(String message) {
        super(message);
    }
}