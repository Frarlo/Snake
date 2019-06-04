package me.ferlo.snake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import static me.ferlo.snake.Constants.*;
import me.ferlo.snake.render.Renderable;

/**
 * Serpente
 * @author ferlin_francesco
 */
public class Pitone implements Renderable {

    private final Snake game;
    
    private int x, y;
    private final List<Quadratino> segmenti = new ArrayList<>();
    private Quadratino lastQuadrato;
    private boolean canChangeDir;
    
    private MoveDirection currDir;
    private Deque<MoveDirection> newDirs = new LinkedBlockingDeque<>(MAX_STORED_MOVES);
    
    public Pitone(Snake game) {
        this.game = game;
    }
    
    public void reset(Quadratino quadratino) {
        segmenti.clear();
        segmenti.add(quadratino);
        
        x = quadratino.getMiddleX();
        y = quadratino.getMiddleY();
        lastQuadrato = null;
        
        currDir = MoveDirection.RIGHT;
        newDirs.clear();
        
        canChangeDir = true;
    }
    
    public void move() throws SeiMortoException {
        switch(currDir) {
            case UP:
                y -= MOVEMENT_SPEED;
                break;
            case DOWN:
                y += MOVEMENT_SPEED;
                break;
            case RIGHT:
                x += MOVEMENT_SPEED;
                break;
            case LEFT:
                x -= MOVEMENT_SPEED;
                break;
        }
        
        if (x < getHead().getMiddleX() - SQUARE_WIDTH / 2 || 
                x > getHead().getMiddleX() + SQUARE_WIDTH / 2
                || y < getHead().getMiddleY() - SQUARE_HEIGHT / 2
                || y > getHead().getMiddleY() + SQUARE_HEIGHT / 2) {
            lastQuadrato = segmenti.get(segmenti.size() - 1);
            
            try {
                if(segmenti.contains(game.getQuadrato(x, y)))
                    throw new SeiMortoException();
                segmenti.remove(segmenti.size() - 1);
                segmenti.add(0, game.getQuadrato(x, y));
                canChangeDir = true;
            } catch(RuntimeException ex) {
                throw new SeiMortoException();
            }
        }
        
        if (canChangeDir && newDirs.peek() != null
                && Math.abs((x % SQUARE_WIDTH) - (SQUARE_WIDTH / 2)) <= MOVEMENT_ERROR_MARGIN
                && Math.abs((y % SQUARE_HEIGHT) - (SQUARE_HEIGHT / 2)) <= MOVEMENT_ERROR_MARGIN) {
            x -= (x % SQUARE_WIDTH) - (SQUARE_WIDTH / 2);
            y -= (y % SQUARE_HEIGHT) - (SQUARE_HEIGHT / 2);
            currDir = newDirs.poll();
            canChangeDir = false;
        }
    }
    
    public void changeDirection(MoveDirection dir) {
        MoveDirection lastDir = newDirs.isEmpty() ? currDir : newDirs.peekLast();
        if(!dir.equals(lastDir) && !dir.equals(lastDir.getOpposto()))
            newDirs.offer(dir);
    }
    
    public void allungaLaSerpe() {
        segmenti.add(lastQuadrato);
    }
    
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.getHSBColor(System.nanoTime() / 10000000000f, 1, 0.95f));
        
        Quadratino lastQuadrato = null;
        for(int i = 0; i < segmenti.size(); i++) {
            Quadratino q = segmenti.get(i);
            if(i != 0) {
                switch(lastQuadrato.getDirection(q)) {
                    case UP:
                        g.fillRect(q.getMiddleX() - 10, q.getMiddleY() - 10, 20, 
                                lastQuadrato.getMiddleY() - q.getMiddleY() + 10);
                        break;
                    case DOWN:
                        g.fillRect(q.getMiddleX() - 10, lastQuadrato.getMiddleY(), 
                                20, q.getMiddleY() - lastQuadrato.getMiddleY() + 10);
                        break;
                    case RIGHT:
                        g.fillRect(lastQuadrato.getMiddleX(), q.getMiddleY() - 10, 
                                q.getMiddleX() - lastQuadrato.getMiddleX() + 10, 20);
                        break;
                    case LEFT:
                        g.fillRect(q.getMiddleX() - 10, q.getMiddleY() - 10, 
                                lastQuadrato.getMiddleX() - q.getMiddleX() + 10, 20);
                        break;
                }
            } else
                g.fillOval(q.getMiddleX() - 10, q.getMiddleY() - 10, 20, 20); 
            lastQuadrato = q;
        }
    }
    
    public Quadratino getHead() {
        return segmenti.get(0);
    }

    @Override
    public int getPriority() {
        return Renderable.HIGH_PRIORITY;
    }
}
