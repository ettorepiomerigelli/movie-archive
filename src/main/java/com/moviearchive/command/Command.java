package com.moviearchive.command;

/**
 * Interfaccia Command del pattern Command.
 *
 * Ogni operazione che modifica l'archivio (aggiunta, rimozione, modifica)
 * viene incapsulata in un oggetto Command invece di essere eseguita
 * direttamente sul modello. Questo disaccoppia il Controller da chi
 * effettivamente esegue l'operazione e, soprattutto, rende possibile
 * implementare undo/redo in modo uniforme tramite un CommandManager
 * che si limita a invocare execute()/undo() senza conoscere il
 * dettaglio di ciascuna operazione.
 */
public interface Command {

    void execute();

    void undo();
}
