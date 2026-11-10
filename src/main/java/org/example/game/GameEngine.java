package org.example.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Regras, estado e atualização da partida; não depende da interface gráfica. */
public final class GameEngine {
    private static final int INITIAL_WIDTH = 760;
    private static final int INITIAL_HEIGHT = 620;
    private static final int STAR_COUNT = 100;
    private static final double PLAYER_SPEED = 4.2;
    private static final int PLAYER_FIRE_INTERVAL = 10;
    private static final int ENEMY_FIRE_INTERVAL = 42;

    private final Random random = new Random();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final Ship player = new Ship(new Color(70, 232, 255));
    private final Ship enemy = new Ship(new Color(255, 92, 126));
    private int width = INITIAL_WIDTH;
    private int height = INITIAL_HEIGHT;
    private int score;
    private int tick;
    private GameResult result = GameResult.IN_PROGRESS;

    public GameEngine() {
        updateShipPositions();
        createStars();
    }

    public Ship getPlayer() { return player; }
    public Ship getEnemy() { return enemy; }
    public List<Projectile> getProjectiles() { return List.copyOf(projectiles); }
    public List<Star> getStars() { return List.copyOf(stars); }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getArenaHeight() { return height / 2; }
    public int getScore() { return score; }
    public int getTick() { return tick; }
    public int getElapsedSeconds() { return tick / 60; }
    public GameResult getResult() { return result; }
    public boolean isGameOver() { return result != GameResult.IN_PROGRESS; }

    public void resize(int newWidth, int newHeight) {
        width = Math.max(newWidth, INITIAL_WIDTH);
        height = Math.max(newHeight, INITIAL_HEIGHT);
        updateShipPositions();
        createStars();
    }

    public void update(Set<GameAction> actions) {
        tick++;
        if (isGameOver()) return;

        updatePlayer(actions);
        updateEnemy();
        createProjectiles(actions);
        updateProjectiles();
    }

    public void restart() {
        player.restore();
        enemy.restore();
        projectiles.clear();
        score = 0;
        tick = 0;
        result = GameResult.IN_PROGRESS;
        updateShipPositions();
    }

    private void updatePlayer(Set<GameAction> actions) {
        double dx = 0;
        double dy = 0;
        if (actions.contains(GameAction.MOVE_LEFT)) dx -= PLAYER_SPEED;
        if (actions.contains(GameAction.MOVE_RIGHT)) dx += PLAYER_SPEED;
        if (actions.contains(GameAction.MOVE_UP)) dy -= PLAYER_SPEED;
        if (actions.contains(GameAction.MOVE_DOWN)) dy += PLAYER_SPEED;
        player.moveBy(dx, dy);

        int arenaHeight = getArenaHeight();
        player.clamp(30, width - 30, 36, arenaHeight - 35);
    }

    private void updateEnemy() {
        double arenaHeight = getArenaHeight();
        double targetX = width / 2.0 + Math.sin(tick * .025) * width * .27;
        double deltaX = Math.max(-2.5, Math.min(2.5, targetX - enemy.getX()));
        double enemyY = arenaHeight + arenaHeight * .30 + Math.sin(tick * .018) * 18;
        enemy.moveBy(deltaX, enemyY - enemy.getY());
        enemy.clamp(35, width - 35, arenaHeight + 1, height - 35);
    }

    private void createProjectiles(Set<GameAction> actions) {
        if (actions.contains(GameAction.FIRE) && tick % PLAYER_FIRE_INTERVAL == 0) {
            projectiles.add(new Projectile(player.getX(), player.getY() + 23, 8.5, Projectile.Owner.PLAYER));
        }
        if (tick % ENEMY_FIRE_INTERVAL == 0) {
            projectiles.add(new Projectile(enemy.getX(), enemy.getY() - 23, -5.4, Projectile.Owner.ENEMY));
        }
    }

    private void updateProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update();
            if (isOutsideArena(projectile)) {
                iterator.remove();
                continue;
            }

            Ship target = projectile.getOwner() == Projectile.Owner.PLAYER ? enemy : player;
            if (hits(projectile, target)) {
                target.takeDamage(projectile.getOwner() == Projectile.Owner.PLAYER ? 8 : 12);
                iterator.remove();
                if (target.getHealth() == 0) {
                    result = target == enemy ? GameResult.PLAYER_WON : GameResult.PLAYER_LOST;
                } else if (projectile.getOwner() == Projectile.Owner.PLAYER) {
                    score += 10;
                }
            }
        }
    }

    private boolean isOutsideArena(Projectile projectile) {
        return projectile.getY() < -20 || projectile.getY() > height + 20;
    }

    private boolean hits(Projectile projectile, Ship target) {
        return Math.abs(projectile.getX() - target.getX()) < 25
                && Math.abs(projectile.getY() - target.getY()) < 23;
    }

    private void updateShipPositions() {
        int arenaHeight = getArenaHeight();
        player.setPosition(width / 2.0, arenaHeight * .70);
        enemy.setPosition(width / 2.0, arenaHeight * 1.30);
    }

    private void createStars() {
        stars.clear();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Star(random.nextInt(width), random.nextInt(height), .4 + random.nextDouble() * 1.8));
        }
    }
}
