package me.ferlo.snake;

import java.awt.Color;
import java.awt.Graphics;
import static me.ferlo.snake.Constants.SQUARE_HEIGHT;
import static me.ferlo.snake.Constants.SQUARE_WIDTH;
import me.ferlo.snake.render.Renderable;

/**
 * Classe per gestire i quadratini
 * @author ferlin_francesco
 */
public class Quadratino implements Renderable {
    /**
     * Coords
     */
    private final int x, y;
    /**
     * Dimension
     */
    private final int width, height;
    /**
     * Colore del quadrato
     */
    private final Color color;
    private boolean hasMela;
    
    Quadratino(int x, int y, int width, int height, Color color) {
       this.x = x;
       this.y = y;
       this.width = width;
       this.height = height;
       this.color = color;
    }
    
    @Override
    public void paint(Graphics g) {
        //g.setColor(color);
        //g.fillRect(x, y, width, height);
        
        if(hasMela) {
            g.setColor(Color.red);
            g.fillOval(getMiddleX() - 6, getMiddleY() - 6, 12, 12);
        }
    }
    
    public int getMiddleX() {
        return x + width / 2;
    }
    
    public int getMiddleY() {
        return y + height / 2;
    }
    
    public boolean hasMela() {
        return hasMela;
    }
    
    public void setMela(boolean mela) {
        hasMela = mela;
    }
    
    public MoveDirection getDirection(Quadratino q2) {
        int xDiff = getMiddleX() - q2.getMiddleX(), 
                yDiff = getMiddleY() - q2.getMiddleY();
        if(xDiff == SQUARE_WIDTH)
            return MoveDirection.LEFT;
        else if(xDiff == -SQUARE_WIDTH)
            return MoveDirection.RIGHT;
        else if(yDiff == SQUARE_HEIGHT)
            return MoveDirection.UP;
        else
            return MoveDirection.DOWN;
    }

    @Override
    public int getPriority() {
        return Renderable.NORMAL_PRIORITY;
    }
}
