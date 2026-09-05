package com.moviearchive.model;

import com.moviearchive.factory.MovieFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    void recreateMovie_preservesGivenId() {
        Movie movie = MovieFactory.recreateMovie("existing-id", "Titolo", "Regista",
                2020, Genre.DRAMA, 4, ViewingStatus.WATCHED);

        assertEquals("existing-id", movie.getId());
    }


    @ParameterizedTest
    @ValueSource(ints = {-100, -5, -1, 6, 7, 10, 100})
    void createNewMovie_withRatingOutOfRange_throwsException(int invalidRating) {
        assertThrows(IllegalArgumentException.class, () ->
                MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                        Genre.DRAMA, invalidRating, ViewingStatus.WATCHED));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void createNewMovie_withRatingInValidRange_andStatusWatched_isAccepted(int validRating) {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, validRating, ViewingStatus.WATCHED);

        assertEquals(validRating, movie.getPersonalRating());
    }


    @ParameterizedTest
    @NullSource
    @EnumSource(value = ViewingStatus.class, names = {"PLANNED", "IN_PROGRESS"})
    void createNewMovie_withNonZeroRating_andNotWatchedStatus_throwsException(ViewingStatus notWatchedStatus) {
        assertThrows(IllegalArgumentException.class, () ->
                MovieFactory.createNewMovie("Titolo", "Regista", 2020, Genre.DRAMA, 5, notWatchedStatus));
    }

    @ParameterizedTest
    @EnumSource(ViewingStatus.class)
    void createNewMovie_withZeroRating_isAcceptedForAnyStatus(ViewingStatus anyStatus) {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 0, anyStatus);

        assertEquals(0, movie.getPersonalRating());
        assertEquals(anyStatus, movie.getStatus());
    }
}