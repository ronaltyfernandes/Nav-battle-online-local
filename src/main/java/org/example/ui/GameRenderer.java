package org.example.ui;

import org.example.game.GameEngine;
import org.example.game.GameResult;
import org.example.game.Projectile;
import org.example.game.Ship;
import org.example.game.Star;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/** Desenha o estado atual da partida sem alterar as regras do jogo. */
public final class GameRenderer {
    private static final Color BACKGROUND = new Color(4, 8, 20);

    public void render(Graphics2D graphics, int width, int height, GameEngine engine) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(BACKGROUND);
        graphics.fillRect(0, 0, width, height);

        int arenaHeight = height / 2;
        drawArena(graphics, 0, arenaHeight, true, engine);
        drawArena(graphics, arenaHeight, height - arenaHeight, false, engine);
        drawDivider(graphics, width, arenaHeight);
        drawShip(graphics, engine.getPlayer(), false);
        drawShip(graphics, engine.getEnemy(), true);
        for (Projectile projectile : engine.getProjectiles()) drawProjectile(graphics, projectile);
        drawHud(graphics, width, height, engine);
        if (engine.isGameOver()) drawEndScreen(graphics, width, height, engine.getResult());
    }

    private void drawArena(Graphics2D graphics, int y, int arenaHeight, boolean upper, GameEngine engine) {
        Color start = upper ? new Color(8, 16, 38) : new Color(12, 10, 33);
        graphics.setPaint(new GradientPaint(0, y, start, 0, y + arenaHeight, BACKGROUND));
        graphics.fillRect(0, y, engine.getWidth(), arenaHeight);

        for (Star star : engine.getStars()) {
            if ((star.getY() < engine.getArenaHeight()) != upper) continue;
            float alpha = (float) (.25 + .65 * (Math.sin(engine.getTick() * .035 + star.getX()) + 1) / 2);
            graphics.setColor(new Color(160, 208, 255, (int) (alpha * 220)));
            int diameter = star.getSize() > 1.7 ? 2 : 1;
            graphics.fillOval(star.getX(), star.getY(), diameter, diameter);
        }

        graphics.setColor(new Color(92, 136, 213, 18));
        for (int x = 0; x < engine.getWidth(); x += 56) graphics.drawLine(x, y, x - 100, y + arenaHeight);
        graphics.setColor(new Color(120, 165, 255, 18));
        for (int gridY = y + 32; gridY < y + arenaHeight; gridY += 48) {
            graphics.drawLine(0, gridY, engine.getWidth(), gridY);
        }
    }

    private void drawDivider(Graphics2D graphics, int width, int y) {
        graphics.setPaint(new GradientPaint(0, y - 2, new Color(61, 221, 255, 0), 0, y + 2,
                new Color(61, 221, 255, 210)));
        graphics.fillRect(0, y - 2, width, 4);
        graphics.setColor(new Color(65, 222, 255, 35));
        graphics.fillRect(0, y - 9, width, 18);
    }

    private void drawShip(Graphics2D graphics, Ship ship, boolean pointsUp) {
        Color color = ship.getColor();
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        graphics.fillOval((int) ship.getX() - 33, (int) ship.getY() - 33, 66, 66);

        Path2D shape = new Path2D.Double();
        if (pointsUp) {
            shape.moveTo(ship.getX(), ship.getY() - 22);
            shape.lineTo(ship.getX() + 19, ship.getY() + 17);
            shape.lineTo(ship.getX(), ship.getY() + 10);
            shape.lineTo(ship.getX() - 19, ship.getY() + 17);
        } else {
            shape.moveTo(ship.getX(), ship.getY() + 22);
            shape.lineTo(ship.getX() + 19, ship.getY() - 17);
            shape.lineTo(ship.getX(), ship.getY() - 10);
            shape.lineTo(ship.getX() - 19, ship.getY() - 17);
        }
        shape.closePath();
        graphics.setColor(color);
        graphics.fill(shape);
        graphics.setColor(new Color(235, 250, 255));
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.draw(shape);
        graphics.setColor(new Color(255, 255, 255, 190));
        graphics.fillOval((int) ship.getX() - 3, (int) ship.getY() - 3, 6, 6);
    }

    private void drawProjectile(Graphics2D graphics, Projectile projectile) {
        Color color = projectile.getOwner() == Projectile.Owner.PLAYER
                ? new Color(85, 239, 255) : new Color(255, 91, 125);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        graphics.fillOval((int) projectile.getX() - 7, (int) projectile.getY() - 10, 14, 20);
        graphics.setColor(color);
        graphics.fillRoundRect((int) projectile.getX() - 2, (int) projectile.getY() - 7, 4, 14, 4, 4);
    }

    private void drawHud(Graphics2D graphics, int width, int height, GameEngine engine) {
        graphics.setFont(new Font("SansSerif", Font.BOLD, 12));
        graphics.setColor(new Color(130, 220, 255));
        graphics.drawString("01  //  PILOTO", 24, 29);
        graphics.setColor(new Color(255, 133, 157));
        graphics.drawString("02  //  RIVAL", 24, engine.getArenaHeight() + 29);
        drawHealthBar(graphics, 24, 40, 160, engine.getPlayer());
        drawHealthBar(graphics, 24, engine.getArenaHeight() + 40, 160, engine.getEnemy());

        graphics.setColor(new Color(192, 218, 255));
        graphics.setFont(new Font("Monospaced", Font.BOLD, 12));
        String scoreText = "PONTOS  " + String.format("%05d", engine.getScore());
        graphics.drawString(scoreText, width - 24 - graphics.getFontMetrics().stringWidth(scoreText), 29);

        graphics.setFont(new Font("SansSerif", Font.PLAIN, 11));
        graphics.setColor(new Color(159, 180, 215));
        graphics.drawString("WASD / SETAS  mover     ESPAÇO  disparar", 24, height - 16);
        String liveText = "COMBATE EM TEMPO REAL  •  " + engine.getElapsedSeconds() + "s";
        graphics.drawString(liveText, width - 24 - graphics.getFontMetrics().stringWidth(liveText), height - 16);
    }

    private void drawHealthBar(Graphics2D graphics, int x, int y, int width, Ship ship) {
        graphics.setColor(new Color(255, 255, 255, 25));
        graphics.fillRoundRect(x, y, width, 7, 6, 6);
        graphics.setColor(ship.getColor());
        graphics.fillRoundRect(x, y, width * ship.getHealth() / 100, 7, 6, 6);
    }

    private void drawEndScreen(Graphics2D graphics, int width, int height, GameResult result) {
        graphics.setColor(new Color(2, 5, 15, 205));
        graphics.fillRect(0, 0, width, height);
        String message = result == GameResult.PLAYER_WON
                ? "VITÓRIA // setor protegido" : "DERROTA // sua nave foi abatida";
        graphics.setFont(new Font("SansSerif", Font.BOLD, 28));
        graphics.setColor(result == GameResult.PLAYER_WON
                ? new Color(88, 237, 255) : new Color(255, 110, 142));
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(message, (width - metrics.stringWidth(message)) / 2, height / 2 - 8);

        String prompt = "Pressione ENTER para jogar novamente";
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 15));
        graphics.setColor(Color.WHITE);
        metrics = graphics.getFontMetrics();
        graphics.drawString(prompt, (width - metrics.stringWidth(prompt)) / 2, height / 2 + 26);
    }
}
