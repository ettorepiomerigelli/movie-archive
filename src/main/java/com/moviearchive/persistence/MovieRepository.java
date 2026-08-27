package com.moviearchive.persistence;

import com.moviearchive.model.Movie;

import java.util.List;

/**
 * Contratto di persistenza per l'archivio dei film, indipendente dal
 * formato concreto usato su disco. Il Controller dipende solo da questa
 * interfaccia (Dependency Inversion): puo' quindi essere testato con
 * un'implementazione finta (fake/mock) senza toccare alcun file reale.
 */
public interface MovieRepository {

    void saveAll(List<Movie> movies);

    List<Movie> loadAll();
}
