package com.moviearchive;

import com.moviearchive.controller.ArchiveController;
import com.moviearchive.persistence.JsonMovieRepository;
import com.moviearchive.persistence.MovieRepository;
import com.moviearchive.view.MainView;

import javax.swing.*;
import java.nio.file.Path;

/**
 * Entry point dell'applicazione Movie Archive.
 * Costruisce Controller e View nell'ordine corretto per evitare la
 * dipendenza circolare fra i due (il Controller esiste gia' quando la
 * View viene creata; la View viene poi "agganciata" al Controller tramite
 * setViewRefreshCallback).
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieRepository repository = new JsonMovieRepository(Path.of("data", "movies.json"));
            ArchiveController controller = new ArchiveController(repository);

            MainView view = new MainView(controller);
            controller.setViewRefreshCallback(view::refresh);

            view.refresh();
            view.setVisible(true);
        });
    }
}
