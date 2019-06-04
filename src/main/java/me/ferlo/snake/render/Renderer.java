/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferlo.snake.render;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.*;
import javax.swing.JPanel;
import static me.ferlo.snake.Constants.TABLE_WIDTH;
import static me.ferlo.snake.Constants.TABLE_HEIGHT;
import me.ferlo.snake.Snake;

public class Renderer extends JPanel {
    private final List<Renderable> objects = new ArrayList<>();
    
    public Renderer(Renderable... objs) {
        objects.addAll(Arrays.asList(objs));
        Collections.sort(objects, Comparator.comparingInt(Renderable::getPriority));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        final int startX = (getWidth() - TABLE_WIDTH) / 2, 
                startY = (getHeight() - TABLE_HEIGHT) / 2;
        
        g.translate(startX, startY);
        g.drawRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
        objects.forEach(r -> r.paint(g));
        g.translate(-startX, -startY);
        
        drawCenteredString(g, "Punteggio: " + Snake.getInstance().getScore(), 
                getWidth() / 2, getHeight() - 20);
    }
    
    /**
     * Mica l'ho copiato cit.
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @author https://stackoverflow.com/a/27740330
     */
    public void drawCenteredString(Graphics g, String text, int x, int y) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // Determine the X coordinate for the text
        int xd = x - metrics.stringWidth(text) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int yd = y - metrics.getHeight() / 2 + metrics.getAscent();
        // Draw the String
        g.drawString(text, xd, yd);
    }
}
