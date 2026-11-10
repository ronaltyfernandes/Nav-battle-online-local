package org.example.game;

/** Ponto decorativo do fundo espacial. */
public final class Star {
    private final int x;
    private final int y;
    private final double size;

    public Star(int x, int y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public double getSize() { return size; }
}
