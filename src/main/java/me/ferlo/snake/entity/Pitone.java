package me.ferlo.snake.entity;

import me.ferlo.snake.util.MoveDirection;
import me.ferlo.snake.util.SeiMortoException;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import static me.ferlo.snake.Constants.*;

/**
 * Serpente
 * @author ferlin_francesco
 */
public class Pitone extends BaseEntity<Pitone> {

    private final List<Quadratino> segmenti;
    private final List<Quadratino> segmentiUnmodifiable;

    private int x, y;
    private boolean canChangeDir;

    private MoveDirection currDir;
    private final Deque<MoveDirection> newDirs;

    private Quadratino lastQuadrato;
    
    public Pitone() {
        super(HIGH_PRIORITY);

        segmenti = new ArrayList<>();
        segmentiUnmodifiable = Collections.unmodifiableList(segmenti);

        newDirs = new LinkedBlockingDeque<>(MAX_STORED_MOVES);
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

    @Override
    public void onTick() {
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
                if(segmenti.contains(game.getEntityManager().getQuadrato(x, y)))
                    throw new SeiMortoException();

                segmenti.remove(segmenti.size() - 1);
                segmenti.add(0, game.getEntityManager().getQuadrato(x, y));
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

        if (getHead().hasMela()) {
            getHead().setMela(false);
            allungaLaSerpe();
            game.melaMangiata();
        }
    }

    @Override
    public void onKeyPress(int keyCode) {
        switch(keyCode) {
            case KeyEvent.VK_UP:
                changeDirection(MoveDirection.UP);
                break;
            case KeyEvent.VK_DOWN:
                changeDirection(MoveDirection.DOWN);
                break;
            case KeyEvent.VK_RIGHT:
                changeDirection(MoveDirection.RIGHT);
                break;
            case KeyEvent.VK_LEFT:
                changeDirection(MoveDirection.LEFT);
                break;
        }
    }

    private void changeDirection(MoveDirection dir) {
        MoveDirection lastDir = newDirs.isEmpty() ? currDir : newDirs.peekLast();
        if(!dir.equals(lastDir) && !dir.equals(lastDir.getOpposto()))
            newDirs.offer(dir);
    }

    private void allungaLaSerpe() {
        segmenti.add(lastQuadrato);
    }

    // Getters


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public MoveDirection getCurrDir() {
        return currDir;
    }

    public Quadratino getHead() {
        return segmenti.get(0);
    }

    public List<Quadratino> getSegmenti() {
        return segmentiUnmodifiable;
    }
}
