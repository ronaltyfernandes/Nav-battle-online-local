package org.example.game;

import java.awt.Color;

/** Estado e operações básicas de uma nave. */
public final class Ship {
    private static final int MAX_HEALTH = 100;
    private double x;
    private double y;
    private int health;
    private final Color color;

    public Ship(Color color) {
        this.color = color;
        this.health = MAX_HEALTH;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public Color getColor() { return color; }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }

    public void moveBy(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void clamp(double minX, double maxX, double minY, double maxY) {
        x = Math.max(minX, Math.min(maxX, x));
        y = Math.max(minY, Math.min(maxY, y));
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    public void restore() { health = MAX_HEALTH; }
}
