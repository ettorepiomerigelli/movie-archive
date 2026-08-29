package com.moviearchive.filter;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeMovieFilterTest {

    private final Movie inception = MovieFactory.createNewMovie(
            "Inception", "Christopher Nolan", 2010, Genre.SCI_FI, 5, ViewingStatus.WATCHED);
    private final Movie amelie = MovieFactory.createNewMovie(
            "Amelie", "Jean-Pierre Jeunet", 2001, Genre.ROMANCE, 0, ViewingStatus.PLANNED);

    @Test
    void emptyComposite_matchesEverything() {
        CompositeMovieFilter filter = new CompositeMovieFilter();

        assertTrue(filter.matches(inception));
        assertTrue(filter.matches(amelie));
    }

    @Test
    void singleGenreFilter_matchesOnlyThatGenre() {
        CompositeMovieFilter filter = new CompositeMovieFilter().add(new GenreFilter(Genre.SCI_FI));

        assertTrue(filter.matches(inception));
        assertFalse(filter.matches(amelie));
    }

    @Test
    void combinedFilters_applyLogicalAnd() {
        CompositeMovieFilter filter = new CompositeMovieFilter()
                .add(new GenreFilter(Genre.SCI_FI))
                .add(new StatusFilter(ViewingStatus.PLANNED)); // inception e' WATCHED, non PLANNED

        assertFalse(filter.matches(inception));
        assertFalse(filter.matches(amelie));
    }

    @Test
    void textSearchFilter_matchesTitleOrDirectorCaseInsensitive() {
        CompositeMovieFilter filter = new CompositeMovieFilter().add(new TextSearchFilter("nolan"));

        assertTrue(filter.matches(inception));
        assertFalse(filter.matches(amelie));
    }

    @Test
    void textSearchFilter_withBlankQuery_matchesEverything() {
        CompositeMovieFilter filter = new CompositeMovieFilter().add(new TextSearchFilter("   "));

        assertTrue(filter.matches(inception));
        assertTrue(filter.matches(amelie));
    }
}
