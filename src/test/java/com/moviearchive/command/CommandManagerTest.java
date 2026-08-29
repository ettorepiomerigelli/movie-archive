package com.moviearchive.command;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.MovieArchive;
import com.moviearchive.model.ViewingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    private MovieArchive archive;
    private CommandManager commandManager;

    @BeforeEach
    void setUp() {
        archive = new MovieArchive();
        commandManager = new CommandManager();
    }

    @Test
    void executeCommand_addsMovieToArchive() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);

        commandManager.executeCommand(new AddMovieCommand(archive, movie));

        assertEquals(1, archive.size());
    }

    @Test
    void undo_afterAdd_removesMovieAgain() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        commandManager.executeCommand(new AddMovieCommand(archive, movie));

        commandManager.undo();

        assertEquals(0, archive.size());
        assertTrue(commandManager.canRedo());
    }

    @Test
    void redo_afterUndo_reappliesCommand() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 3, ViewingStatus.WATCHED);
        commandManager.executeCommand(new AddMovieCommand(archive, movie));
        commandManager.undo();

        commandManager.redo();

        assertEquals(1, archive.size());
        assertFalse(commandManager.canRedo());
    }

    @Test
    void executeCommand_afterUndo_clearsRedoStack() {
        Movie a = MovieFactory.createNewMovie("A", "Regista", 2020, Genre.DRAMA, 3, ViewingStatus.WATCHED);
        Movie b = MovieFactory.createNewMovie("B", "Regista", 2021, Genre.DRAMA, 3, ViewingStatus.WATCHED);
        commandManager.executeCommand(new AddMovieCommand(archive, a));
        commandManager.undo();

        commandManager.executeCommand(new AddMovieCommand(archive, b));

        assertFalse(commandManager.canRedo());
    }

    @Test
    void undo_removeCommand_restoresOriginalMovieState() {
        Movie movie = MovieFactory.createNewMovie("Titolo", "Regista", 2020,
                Genre.DRAMA, 4, ViewingStatus.WATCHED);
        archive.add(movie);

        commandManager.executeCommand(new RemoveMovieCommand(archive, movie));
        commandManager.undo();

        Movie restored = archive.findById(movie.getId()).orElseThrow();
        assertEquals(4, restored.getPersonalRating());
    }

    @Test
    void canUndo_isFalse_whenNoCommandsExecuted() {
        assertFalse(commandManager.canUndo());
    }
}
