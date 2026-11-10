package org.example.ui;

import javax.swing.JFrame;
import java.awt.Dimension;

/** Configura e exibe a janela principal do jogo. */
public final class GameWindow {
    private final JFrame frame;

    public GameWindow() {
        frame = new JFrame("NEBULA // Duelo espacial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new GamePanel());
        frame.setMinimumSize(new Dimension(760, 620));
        frame.setSize(1000, 760);
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }
}
