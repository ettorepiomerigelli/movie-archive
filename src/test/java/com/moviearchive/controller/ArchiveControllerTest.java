package com.moviearchive.controller;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.DuplicateMovieException;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;
import com.moviearchive.persistence.MovieRepository;
import com.moviearchive.strategy.SortByYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveControllerTest {

    /**
     * Repository "fake" in memoria: grazie alla Dependency Injection
     * (il repository concreto e' passato dall'esterno, non creato con
     * un Singleton), il Controller si puo' testare senza toccare il
     * file system.
     */
    private static class InMemoryMovieRepository implements MovieRepository {
        private List<Movie> stored = new ArrayList<>();

        @Override
        public void saveAll(List<Movie> movies) {
            this.stored = new ArrayList<>(movies);
        }

        @Override
        public List<Movie> loadAll() {
            return new ArrayList<>(stored);
        }
    }

    private InMemoryMovieRepository repository;
    private ArchiveController controller;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMovieRepository();
        controller = new ArchiveController(repository);
    }

    @Test
    void addMovie_increasesTotalAndPersists() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);

        controller.addMovie(movie);

        assertEquals(1, controller.getTotalMovies());
        assertEquals(1, repository.loadAll().size());
    }

    @Test
    void undo_afterAddMovie_removesItAndUpdatesPersistence() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        controller.addMovie(movie);

        controller.undo();

        assertEquals(0, controller.getTotalMovies());
        assertTrue(repository.loadAll().isEmpty());
        assertTrue(controller.canRedo());
    }

    @Test
    void getVisibleMovies_appliesGenreFilter() {
        controller.addMovie(MovieFactory.createNewMovie("A", "Regista", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED));
        controller.addMovie(MovieFactory.createNewMovie("B", "Regista", 2021, Genre.HORROR, 3, ViewingStatus.WATCHED));

        controller.setGenreFilter(Genre.HORROR);

        List<Movie> visible = controller.getVisibleMovies();
        assertEquals(1, visible.size());
        assertEquals("B", visible.get(0).getTitle());
    }

    @Test
    void getVisibleMovies_appliesSortStrategy() {
        controller.addMovie(MovieFactory.createNewMovie("Vecchio", "Regista", 1990, Genre.DRAMA, 3, ViewingStatus.WATCHED));
        controller.addMovie(MovieFactory.createNewMovie("Nuovo", "Regista", 2022, Genre.DRAMA, 3, ViewingStatus.WATCHED));

        controller.setSortStrategy(new SortByYear()); // piu' recente prima

        List<Movie> visible = controller.getVisibleMovies();
        assertEquals("Nuovo", visible.get(0).getTitle());
        assertEquals("Vecchio", visible.get(1).getTitle());
    }

    @Test
    void removeMovie_removesFromArchiveAndPersistence() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        controller.addMovie(movie);

        controller.removeMovie(movie.getId());

        assertEquals(0, controller.getTotalMovies());
    }

    @Test
    void addMovie_duplicateOfExisting_throwsAndDoesNotAdd() {
        controller.addMovie(MovieFactory.createNewMovie("Inception", "Christopher Nolan",
                2010, Genre.SCI_FI, 5, ViewingStatus.WATCHED));

        Movie duplicate = MovieFactory.createNewMovie("Inception", "Christopher Nolan",
                2010, Genre.ACTION, 0, ViewingStatus.PLANNED);

        assertThrows(DuplicateMovieException.class, () -> controller.addMovie(duplicate));
        assertEquals(1, controller.getTotalMovies()); // il duplicato non deve essere stato aggiunto
    }

    @Test
    void updateMovie_intoDuplicateOfAnotherExisting_throwsAndDoesNotApply() {
        Movie a = MovieFactory.createNewMovie("A", "Regista Uno", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED);
        Movie b = MovieFactory.createNewMovie("B", "Regista Due", 2021, Genre.COMEDY, 4, ViewingStatus.PLANNED);
        controller.addMovie(a);
        controller.addMovie(b);

        // Si tenta di modificare "B" rendendolo equivalente ad "A"
        Movie bMadeDuplicate = MovieFactory.recreateMovie(b.getId(), "A", "Regista Uno",
                2020, Genre.COMEDY, 4, ViewingStatus.PLANNED);

        assertThrows(DuplicateMovieException.class, () -> controller.updateMovie(bMadeDuplicate));
        assertEquals("B", controller.getMovieById(b.getId()).get().getTitle());
    }

    @Test
    void updateMovie_withUnchangedTitleDirectorYear_doesNotThrow() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        controller.addMovie(movie);

        // Si modifica solo la valutazione, titolo/regista/anno restano gli stessi:
        // non deve essere considerato un duplicato di se stesso.
        Movie edited = MovieFactory.recreateMovie(movie.getId(), "Titolo", "Regista",
                2020, Genre.DRAMA, 5, ViewingStatus.WATCHED);

        assertDoesNotThrow(() -> controller.updateMovie(edited));
        assertEquals(5, controller.getMovieById(movie.getId()).get().getPersonalRating());
    }
}