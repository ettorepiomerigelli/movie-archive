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
}
