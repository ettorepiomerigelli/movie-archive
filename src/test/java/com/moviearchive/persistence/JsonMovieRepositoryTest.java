package com.moviearchive.persistence;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonMovieRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void loadAll_onMissingFile_returnsEmptyList() {
        JsonMovieRepository repository = new JsonMovieRepository(tempDir.resolve("archivio.json"));

        List<Movie> movies = repository.loadAll();

        assertTrue(movies.isEmpty());
    }

    @Test
    void saveAll_thenLoadAll_roundTripsMovieData() {
        JsonMovieRepository repository = new JsonMovieRepository(tempDir.resolve("archivio.json"));
        Movie movie = MovieFactory.createNewMovie("Il Signore degli Anelli", "Peter Jackson",
                2001, Genre.FANTASY, 5, ViewingStatus.WATCHED);

        repository.saveAll(List.of(movie));
        List<Movie> loaded = repository.loadAll();

        assertEquals(1, loaded.size());
        Movie reloaded = loaded.get(0);
        assertEquals(movie.getId(), reloaded.getId());
        assertEquals(movie.getTitle(), reloaded.getTitle());
        assertEquals(movie.getDirector(), reloaded.getDirector());
        assertEquals(movie.getReleaseYear(), reloaded.getReleaseYear());
        assertEquals(movie.getGenre(), reloaded.getGenre());
        assertEquals(movie.getPersonalRating(), reloaded.getPersonalRating());
        assertEquals(movie.getStatus(), reloaded.getStatus());
    }

    @Test
    void saveAll_withTitleContainingQuotes_escapesAndReloadsCorrectly() {
        JsonMovieRepository repository = new JsonMovieRepository(tempDir.resolve("archivio.json"));
        Movie movie = MovieFactory.createNewMovie("Film \"Speciale\"", "Regista", 2020,
                Genre.DRAMA, 0, ViewingStatus.PLANNED);

        repository.saveAll(List.of(movie));
        List<Movie> loaded = repository.loadAll();

        assertEquals("Film \"Speciale\"", loaded.get(0).getTitle());
    }

    @Test
    void saveAll_withEmptyList_producesFileThatLoadsAsEmpty() {
        JsonMovieRepository repository = new JsonMovieRepository(tempDir.resolve("archivio.json"));

        repository.saveAll(List.of());
        List<Movie> loaded = repository.loadAll();

        assertTrue(loaded.isEmpty());
    }
}
