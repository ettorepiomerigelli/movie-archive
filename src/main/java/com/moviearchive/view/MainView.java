package com.moviearchive.view;

import com.moviearchive.controller.ArchiveController;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;
import com.moviearchive.strategy.SortByRating;
import com.moviearchive.strategy.SortByTitle;
import com.moviearchive.strategy.SortByYear;
import com.moviearchive.strategy.SortStrategy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Finestra principale dell'applicazione (View del pattern MVC).
 *
 * A differenza di una singola JTable con toolbar, qui i controlli di
 * ricerca/filtro/ordinamento sono raggruppati in un pannello laterale
 * sinistro, con una barra in alto dedicata alle azioni sui film
 * (nuovo/modifica/elimina) e alla history (annulla/ripeti). La View non
 * contiene alcuna logica di business: si limita a raccogliere l'input
 * dell'utente e a delegarlo al Controller, e a ridisegnarsi quando il
 * Controller la richiama dopo un cambiamento di stato.
 */
public class MainView extends JFrame {

    private final ArchiveController controller;

    private final MovieTableModel tableModel = new MovieTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField searchField = new JTextField(16);
    private final JComboBox<String> genreFilterCombo = new JComboBox<>();
    private final JComboBox<String> statusFilterCombo = new JComboBox<>();
    private final JComboBox<SortStrategy> sortCombo = new JComboBox<>(
            new SortStrategy[]{new SortByTitle(), new SortByYear(), new SortByRating()});

    private final JButton newButton = new JButton("Nuovo film");
    private final JButton editButton = new JButton("Modifica");
    private final JButton deleteButton = new JButton("Elimina");
    private final JButton undoButton = new JButton("\u21B6 Annulla");
    private final JButton redoButton = new JButton("\u21B7 Ripeti");
    private final JLabel statusLabel = new JLabel(" ");

    public MainView(ArchiveController controller) {
        super("Movie Archive");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildSidePanel(), BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateButtonStates());
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setIntercellSpacing(new Dimension(0, 1));

        wireActions();
        updateButtonStates();
    }

    private JToolBar buildToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(newButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(undoButton);
        toolBar.add(redoButton);
        return toolBar;
    }

    private JPanel buildSidePanel() {
        genreFilterCombo.addItem("Tutti i generi");
        for (Genre genre : Genre.values()) {
            genreFilterCombo.addItem(genre.getLabel());
        }

        statusFilterCombo.addItem("Qualsiasi stato");
        for (ViewingStatus status : ViewingStatus.values()) {
            statusFilterCombo.addItem(status.getLabel());
        }

        capHeight(searchField);
        capHeight(genreFilterCombo);
        capHeight(statusFilterCombo);
        capHeight(sortCombo);
        searchField.setToolTipText("Cerca per titolo o regista");

        JLabel genreLabel = new JLabel("Genere:");
        genreLabel.setToolTipText("Filtra per genere");
        JLabel statusLabel2 = new JLabel("Stato:");
        statusLabel2.setToolTipText("Filtra per stato di visione");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(230, 0));

        panel.add(new JLabel("Cerca:"));
        panel.add(searchField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(genreLabel);
        panel.add(genreFilterCombo);
        panel.add(Box.createVerticalStrut(12));
        panel.add(statusLabel2);
        panel.add(statusFilterCombo);
        panel.add(Box.createVerticalStrut(12));
        panel.add(new JLabel("Ordina per:"));
        panel.add(sortCombo);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Impedisce a un componente di espandersi oltre la propria altezza
     * preferita quando è inserito in un BoxLayout verticale con spazio
     * libero.
     */
    private void capHeight(JComponent component) {
        component.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void wireActions() {
        newButton.addActionListener(e -> onNewMovie());
        editButton.addActionListener(e -> onEditMovie());
        deleteButton.addActionListener(e -> onDeleteMovie());
        undoButton.addActionListener(e -> {
            controller.undo();
            updateButtonStates();
        });
        redoButton.addActionListener(e -> {
            controller.redo();
            updateButtonStates();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            public void changedUpdate(DocumentEvent e) { applySearch(); }

            private void applySearch() {
                controller.setSearchQuery(searchField.getText());
            }
        });

        genreFilterCombo.addActionListener(e -> {
            int index = genreFilterCombo.getSelectedIndex();
            Genre selected = (index <= 0) ? null : Genre.values()[index - 1];
            controller.setGenreFilter(selected);
        });

        statusFilterCombo.addActionListener(e -> {
            int index = statusFilterCombo.getSelectedIndex();
            ViewingStatus selected = (index <= 0) ? null : ViewingStatus.values()[index - 1];
            controller.setStatusFilter(selected);
        });

        sortCombo.addActionListener(e ->
                controller.setSortStrategy((SortStrategy) sortCombo.getSelectedItem()));
    }

    private void onNewMovie() {
        MovieFormDialog dialog = new MovieFormDialog(this, null);
        dialog.setVisible(true);
        Movie created = dialog.getResult();
        if (created != null) {
            controller.addMovie(created);
        }
    }

    private void onEditMovie() {
        Movie selected = getSelectedMovie();
        if (selected == null) {
            return;
        }
        MovieFormDialog dialog = new MovieFormDialog(this, selected);
        dialog.setVisible(true);
        Movie edited = dialog.getResult();
        if (edited != null) {
            controller.updateMovie(edited);
        }
    }

    private void onDeleteMovie() {
        Movie selected = getSelectedMovie();
        if (selected == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Eliminare \"" + selected.getTitle() + "\" dall'archivio?",
                "Conferma eliminazione", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeMovie(selected.getId());
        }
    }

    private Movie getSelectedMovie() {
        int row = table.getSelectedRow();
        return (row == -1) ? null : tableModel.getMovieAt(row);
    }

    private void updateButtonStates() {
        boolean hasSelection = table.getSelectedRow() != -1;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        undoButton.setEnabled(controller.canUndo());
        redoButton.setEnabled(controller.canRedo());
    }

    /**
     * Richiamato dal Controller (tramite callback) ogni volta che
     * l'archivio o i criteri di visualizzazione cambiano.
     */
    public void refresh() {
        tableModel.setMovies(controller.getVisibleMovies());
        statusLabel.setText("  Film totali in archivio: " + controller.getTotalMovies()
                + "   |   Film mostrati: " + tableModel.getRowCount());
        updateButtonStates();
    }
}