package me.ferlo.snake.render.swing;

import me.ferlo.snake.render.RenderContext;

import java.awt.*;

public class SwingContext implements RenderContext {

    private final Graphics2D g2d;

    public SwingContext(Graphics2D graphics) {
        this.g2d = graphics;
    }

    public Graphics2D getGraphics() {
        return g2d;
    }

    /**
     * Mica l'ho copiato cit.
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param text The String to draw.
     * @author https://stackoverflow.com/a/27740330
     */
    public void drawCenteredString(String text, int x, int y) {
        // Get the FontMetrics
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        // Determine the X coordinate for the text
        int xd = x - metrics.stringWidth(text) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int yd = y - metrics.getHeight() / 2 + metrics.getAscent();
        // Draw the String
        g2d.drawString(text, xd, yd);
    }
}
