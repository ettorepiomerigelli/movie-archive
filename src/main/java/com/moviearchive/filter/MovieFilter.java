package com.moviearchive.filter;

import com.moviearchive.model.Movie;

/**
 * Componente del pattern Composite applicato ai criteri di filtro.

 * Ogni criterio è invece un oggetto MovieFilter indipendente, e piu'
 * criteri attivi vengono combinati in un CompositeMovieFilter: aggiungere
 * un nuovo tipo di filtro in futuro (es. per intervallo di anni) richiede
 * solo una nuova classe che implementa questa interfaccia, senza toccare
 * il codice del Controller.
 */
public interface MovieFilter {

    boolean matches(Movie movie);
}
