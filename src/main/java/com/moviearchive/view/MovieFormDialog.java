package com.moviearchive.view;

import com.moviearchive.factory.MovieFactory;
import com.moviearchive.model.Genre;
import com.moviearchive.model.Movie;
import com.moviearchive.model.ViewingStatus;

import javax.swing.*;
import java.awt.*;
import java.time.Year;

/**
 * Finestra di dialogo modale per l'inserimento o la modifica di un film.
 * In modalita' modifica riceve il Movie esistente e ne pre-compila i campi;
 * la validazione vera e propria e' comunque delegata a MovieFactory,
 * cosi' che le regole di validita' vivano in un unico posto.
 */
public class MovieFormDialog extends JDialog {

    private final JTextField titleField = new JTextField(20);
    private final JTextField directorField = new JTextField(20);
    private final JSpinner yearSpinner;
    private final JComboBox<Genre> genreCombo = new JComboBox<>(Genre.values());
    private final JSpinner ratingSpinner = new JSpinner(
            new SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(1)));
    private final JComboBox<ViewingStatus> statusCombo = new JComboBox<>(ViewingStatus.values());

    private Movie result;
    private final String editingId; // null se e' un nuovo inserimento

    public MovieFormDialog(Frame owner, Movie existingMovie) {
        super(owner, existingMovie == null ? "Nuovo film" : "Modifica film", true);
        this.editingId = (existingMovie != null) ? existingMovie.getId() : null;

        int currentYear = Year.now().getValue();
        yearSpinner = new JSpinner(new SpinnerNumberModel(
                Integer.valueOf(currentYear), null, null, Integer.valueOf(1)));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));

        if (existingMovie != null) {
            titleField.setText(existingMovie.getTitle());
            directorField.setText(existingMovie.getDirector());
            yearSpinner.setValue(existingMovie.getReleaseYear());
            genreCombo.setSelectedItem(existingMovie.getGenre());
            ratingSpinner.setValue(existingMovie.getPersonalRating());
            statusCombo.setSelectedItem(existingMovie.getStatus());
        }

        setLayout(new BorderLayout(10, 10));
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, c, 0, "Titolo:", titleField);
        addRow(panel, c, 1, "Regista:", directorField);
        addRow(panel, c, 2, "Anno di uscita:", yearSpinner);
        addRow(panel, c, 3, "Genere:", genreCombo);
        addRow(panel, c, 4, "Valutazione (0-5):", ratingSpinner);
        addRow(panel, c, 5, "Stato di visione:", statusCombo);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(field, c);
    }

    private JPanel buildButtonPanel() {
        JButton saveButton = new JButton("Salva");
        JButton cancelButton = new JButton("Annulla");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private void onSave() {
        try {
            yearSpinner.commitEdit();
            ratingSpinner.commitEdit();
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Anno e valutazione devono essere numeri interi validi.",
                    "Dati non validi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Genre genre = (Genre) genreCombo.getSelectedItem();
            ViewingStatus status = (ViewingStatus) statusCombo.getSelectedItem();
            int year = (Integer) yearSpinner.getValue();
            int rating = (Integer) ratingSpinner.getValue();

            result = (editingId == null)
                    ? MovieFactory.createNewMovie(titleField.getText(), directorField.getText(),
                    year, genre, rating, status)
                    : MovieFactory.recreateMovie(editingId, titleField.getText(), directorField.getText(),
                    year, genre, rating, status);

            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dati non validi",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Da chiamare dopo setVisible(true): restituisce il film risultante,
     * oppure null se l'utente ha annullato l'operazione.
     */
    public Movie getResult() {
        return result;
    }
}