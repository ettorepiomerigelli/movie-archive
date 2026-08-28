package com.moviearchive.view;

import com.moviearchive.model.Movie;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel dedicato per mostrare la lista di film in una JTable.
 * Tiene una copia locale della lista "visibile" corrente: il Controller
 * ricalcola tale lista (filtro + ordinamento) e la View si limita a
 * sostituirla e a notificare la tabella con fireTableDataChanged().
 */
public class MovieTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Titolo", "Regista", "Anno", "Genere", "Valutazione", "Stato"
    };

    private List<Movie> movies = new ArrayList<>();

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        fireTableDataChanged();
    }

    public Movie getMovieAt(int row) {
        return movies.get(row);
    }

    @Override
    public int getRowCount() {
        return movies.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Movie movie = movies.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> movie.getTitle();
            case 1 -> movie.getDirector();
            case 2 -> movie.getReleaseYear();
            case 3 -> movie.getGenre().getLabel();
            case 4 -> movie.getPersonalRating() > 0
                    ? "\u2605".repeat(movie.getPersonalRating())
                    : "-";
            case 5 -> movie.getStatus().getLabel();
            default -> "";
        };
    }
}
