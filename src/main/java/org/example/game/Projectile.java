package org.example.game;

/** Projétil que se move verticalmente pela arena. */
public final class Projectile {
    public enum Owner { PLAYER, ENEMY }

    private final double x;
    private final double velocityY;
    private final Owner owner;
    private double y;

    public Projectile(double x, double y, double velocityY, Owner owner) {
        this.x = x;
        this.y = y;
        this.velocityY = velocityY;
        this.owner = owner;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Owner getOwner() { return owner; }
    public void update() { y += velocityY; }
}
