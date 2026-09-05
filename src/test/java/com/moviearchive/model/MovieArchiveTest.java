package com.moviearchive.model;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.observer.ArchiveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieArchiveTest {

    private MovieArchive archive;
    private List<ArchiveEvent> receivedEvents;

    @BeforeEach
    void setUp() {
        archive = new MovieArchive();
        receivedEvents = new ArrayList<>();
        archive.addObserver(receivedEvents::add);
    }

    @Test
    void add_notifiesObserversWithAddedEvent() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);

        archive.add(movie);

        assertEquals(1, archive.size());
        assertEquals(1, receivedEvents.size());
        assertEquals(ArchiveEvent.Type.ADDED, receivedEvents.get(0).getType());
    }

    @Test
    void remove_existingMovie_notifiesObserversWithRemovedEvent() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        archive.add(movie);

        archive.remove(movie.getId());

        assertEquals(0, archive.size());
        assertEquals(ArchiveEvent.Type.REMOVED, receivedEvents.get(1).getType());
    }

    @Test
    void remove_nonExistingMovie_doesNothing() {
        archive.remove("id-inesistente");

        assertEquals(0, receivedEvents.size());
    }

    @Test
    void update_existingMovie_replacesItAndNotifies() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        archive.add(movie);

        movie.setPersonalRating(5);
        archive.update(movie);

        assertEquals(5, archive.findById(movie.getId()).get().getPersonalRating());
        assertEquals(ArchiveEvent.Type.UPDATED, receivedEvents.get(1).getType());
    }

    @Test
    void replaceAll_emitsSingleReloadedEvent() {
        Movie a = MovieFactory.createNewMovie("A", "Regista", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED);
        Movie b = MovieFactory.createNewMovie("B", "Regista", 2021, Genre.DRAMA, 3, ViewingStatus.WATCHED);

        archive.replaceAll(List.of(a, b));

        assertEquals(2, archive.size());
        assertEquals(1, receivedEvents.size());
        assertEquals(ArchiveEvent.Type.RELOADED, receivedEvents.get(0).getType());
    }

    @Test
    void containsDuplicate_sameTitleDirectorYear_returnsTrue() {
        Movie original = MovieFactory.createNewMovie("Inception", "Christopher Nolan",
                2010, Genre.SCI_FI, 5, ViewingStatus.WATCHED);
        archive.add(original);

        Movie candidate = MovieFactory.createNewMovie("inception", "CHRISTOPHER NOLAN",
                2010, Genre.ACTION, 0, ViewingStatus.PLANNED);

        assertTrue(archive.containsDuplicate(candidate));
    }

    @Test
    void containsDuplicate_differentYear_returnsFalse() {
        Movie original = MovieFactory.createNewMovie("Dune", "Denis Villeneuve",
                2021, Genre.SCI_FI, 4, ViewingStatus.WATCHED);
        archive.add(original);

        Movie candidate = MovieFactory.createNewMovie("Dune", "Denis Villeneuve",
                1984, Genre.SCI_FI, 3, ViewingStatus.WATCHED);

        assertFalse(archive.containsDuplicate(candidate));
    }

    @Test
    void containsDuplicate_excludesMovieItself() {
        Movie movie = MovieFactory.createNewMovie("Amelie", "Jean-Pierre Jeunet",
                2001, Genre.ROMANCE, 4, ViewingStatus.WATCHED);
        archive.add(movie);

        // Lo stesso film (stesso id) non deve mai risultare "duplicato di se stesso",
        // altrimenti sarebbe impossibile modificarlo senza cambiarne titolo/regista/anno.
        assertFalse(archive.containsDuplicate(movie));
    }

    // --- Copie difensive in USCITA: mutare un Movie ottenuto da getAll()/
    // findById() non deve mai toccare l'oggetto realmente custodito
    // nell'archivio. Senza queste copie, un chiamante esterno potrebbe
    // modificare l'archivio bypassando Command, Observer e persistenza. ---

    @Test
    void findById_returnsDefensiveCopy_mutatingItDoesNotAffectArchive() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        archive.add(movie);

        Movie reference = archive.findById(movie.getId()).get();
        reference.setPersonalRating(999); // mutazione diretta sul riferimento ottenuto

        assertEquals(3, archive.findById(movie.getId()).get().getPersonalRating(),
                "l'archivio non deve risentire della mutazione di una copia esterna");
    }

    @Test
    void getAll_returnsDefensiveCopies_mutatingThemDoesNotAffectArchive() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        archive.add(movie);

        archive.getAll().get(0).setPersonalRating(999);

        assertEquals(3, archive.findById(movie.getId()).get().getPersonalRating(),
                "l'archivio non deve risentire della mutazione di una copia esterna");
    }

    // --- Copie difensive in ENTRATA: mutare l'oggetto originale DOPO averlo
    // passato ad add()/update()/replaceAll() non deve alterare cio' che
    // l'archivio ha effettivamente memorizzato. ---

    @Test
    void add_copiesInput_mutatingOriginalAfterwardsDoesNotAffectArchive() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);

        archive.add(movie);
        movie.setPersonalRating(999); // mutazione dell'oggetto originale, dopo l'add

        assertEquals(3, archive.findById(movie.getId()).get().getPersonalRating(),
                "l'archivio deve aver memorizzato una copia indipendente al momento dell'add");
    }

    @Test
    void update_copiesInput_mutatingOriginalAfterwardsDoesNotAffectArchive() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        archive.add(movie);

        Movie edited = MovieFactory.recreateMovie(movie.getId(), "Titolo", "Regista",
                2020, Genre.DRAMA, 5, ViewingStatus.WATCHED);
        archive.update(edited);
        edited.setPersonalRating(999); // mutazione dell'oggetto passato, dopo l'update

        assertEquals(5, archive.findById(movie.getId()).get().getPersonalRating(),
                "l'archivio deve aver memorizzato una copia indipendente al momento dell'update");
    }

    @Test
    void replaceAll_copiesInputElements_mutatingOriginalsAfterwardsDoesNotAffectArchive() {
        Movie a = MovieFactory.createNewMovie("A", "Regista", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED);
        List<Movie> toLoad = new ArrayList<>(List.of(a));

        archive.replaceAll(toLoad);
        a.setPersonalRating(999); // mutazione dell'originale, dopo il caricamento

        assertEquals(3, archive.findById(a.getId()).get().getPersonalRating(),
                "l'archivio deve aver copiato ogni film al momento del replaceAll");
    }


    @Test
    void replaceAll_withManyMovies_stillEmitsExactlyOneEvent() {
        List<Movie> many = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            many.add(MovieFactory.createNewMovie("Film " + i, "Regista", 2020,
                    Genre.DRAMA, 0, ViewingStatus.PLANNED));
        }

        archive.replaceAll(many);

        assertEquals(20, archive.size());
        assertEquals(1, receivedEvents.size(),
                "un solo evento RELOADED, indipendentemente dal numero di film caricati");
        assertEquals(ArchiveEvent.Type.RELOADED, receivedEvents.get(0).getType());
    }
}