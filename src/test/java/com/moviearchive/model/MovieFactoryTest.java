package com.moviearchive.model;

import com.moviearchive.factory.MovieFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieFactoryTest {

    @Test
    void createNewMovie_withValidData_setsAllFields() {
        Movie movie = MovieFactory.createNewMovie("Inception", "Christopher Nolan",
                2010, Genre.SCI_FI, 5, ViewingStatus.WATCHED);

        assertNotNull(movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals("Christopher Nolan", movie.getDirector());
        assertEquals(2010, movie.getReleaseYear());
        assertEquals(Genre.SCI_FI, movie.getGenre());
        assertEquals(5, movie.getPersonalRating());
        assertEquals(ViewingStatus.WATCHED, movie.getStatus());
    }

    @Test
    void createNewMovie_withNullGenreAndStatus_appliesDefaults() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020, null, 0, null);

        assertEquals(Genre.UNCLASSIFIED, movie.getGenre());
        assertEquals(ViewingStatus.PLANNED, movie.getStatus());
    }

    @Test
    void createNewMovie_withBlankTitle_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                MovieFactory.createNewMovie("   ", "Regista", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED));
    }

    @Test
    void createNewMovie_withInvalidYear_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                MovieFactory.createNewMovie("Titolo", "Regista", 1700, Genre.DRAMA, 3, ViewingStatus.WATCHED));
    }

    @Test
    void createNewMovie_withRatingOutOfRange_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                MovieFactory.createNewMovie("Titolo", "Regista", 2020, Genre.DRAMA, 8, ViewingStatus.WATCHED));
    }

    @Test
    void recreateMovie_preservesGivenId() {
        Movie movie = MovieFactory.recreateMovie("existing-id", "Titolo", "Regista",
                2020, Genre.DRAMA, 4, ViewingStatus.WATCHED);

        assertEquals("existing-id", movie.getId());
    }
}
