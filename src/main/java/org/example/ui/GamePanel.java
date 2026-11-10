package org.example.ui;

import org.example.game.GameAction;
import org.example.game.GameEngine;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Adaptador Swing: recebe entradas, avança o jogo e solicita a renderização. */
public final class GamePanel extends JPanel {
    private static final int FRAME_DELAY_MS = 16;

    private final GameEngine engine = new GameEngine();
    private final GameRenderer renderer = new GameRenderer();
    private final Timer timer;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Map<Integer, GameAction> movementKeys = new HashMap<>();

    public GamePanel() {
        setPreferredSize(new Dimension(1000, 760));
        setFocusable(true);
        configureKeys();
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) {
                engine.resize(getWidth(), getHeight());
            }
        });
        timer = new Timer(FRAME_DELAY_MS, event -> onFrame());
        timer.start();
    }

    private void configureKeys() {
        bindMovement(KeyEvent.VK_LEFT, GameAction.MOVE_LEFT);
        bindMovement(KeyEvent.VK_A, GameAction.MOVE_LEFT);
        bindMovement(KeyEvent.VK_RIGHT, GameAction.MOVE_RIGHT);
        bindMovement(KeyEvent.VK_D, GameAction.MOVE_RIGHT);
        bindMovement(KeyEvent.VK_UP, GameAction.MOVE_UP);
        bindMovement(KeyEvent.VK_W, GameAction.MOVE_UP);
        bindMovement(KeyEvent.VK_DOWN, GameAction.MOVE_DOWN);
        bindMovement(KeyEvent.VK_S, GameAction.MOVE_DOWN);
        bindMovement(KeyEvent.VK_SPACE, GameAction.FIRE);

        bindKey(KeyEvent.VK_ENTER, "restart", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent event) {
                if (engine.isGameOver()) engine.restart();
            }
        });
    }

    private void bindMovement(int keyCode, GameAction action) {
        movementKeys.put(keyCode, action);
        bindKey(keyCode, "pressed_" + keyCode, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent event) {
                pressedKeys.add(keyCode);
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, true), "released_" + keyCode);
        getActionMap().put("released_" + keyCode, new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent event) {
                pressedKeys.remove(keyCode);
            }
        });
    }

    private void bindKey(int keyCode, String name, AbstractAction action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, false), name);
        getActionMap().put(name, action);
    }

    private void onFrame() {
        EnumSet<GameAction> actions = EnumSet.noneOf(GameAction.class);
        for (int keyCode : pressedKeys) {
            GameAction action = movementKeys.get(keyCode);
            if (action != null) actions.add(action);
        }
        engine.update(actions);
        repaint();
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        try {
            renderer.render(graphics2D, getWidth(), getHeight(), engine);
        } finally {
            graphics2D.dispose();
        }
    }
}
