package com.moviearchive.observer;

/**
 * Interfaccia Observer del pattern Observer.
 * Chi la implementa viene notificato ogni volta che l'archivio dei film
 * cambia stato (aggiunta, rimozione, modifica o ricaricamento completo).
 */
public interface ArchiveObserver {

    void onArchiveChanged(ArchiveEvent event);
}
