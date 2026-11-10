package org.example;

import javax.swing.SwingUtilities;
import org.example.ui.GameWindow;

/** Ponto de entrada da aplicação. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow().show());
    }
}
