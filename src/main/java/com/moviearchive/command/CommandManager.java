package com.moviearchive.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Invoker del pattern Command. Mantiene due pile (undo/redo) e si occupa
 * di eseguire i comandi mantenendo la history coerente:
 *  - eseguire un nuovo comando svuota la pila di redo (una nuova azione
 *    dopo un undo rende non piu' sensato "rifare" cio' che era stato
 *    annullato);
 *  - undo() sposta il comando dalla pila undo alla pila redo, e viceversa.
 */
public class CommandManager {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void undo() {
        if (!canUndo()) {
            return;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    public void redo() {
        if (!canRedo()) {
            return;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }
}
