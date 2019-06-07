package me.ferlo.snake.entity;

import me.ferlo.snake.util.MoveDirection;

import java.awt.*;

import static me.ferlo.snake.Constants.SQUARE_HEIGHT;
import static me.ferlo.snake.Constants.SQUARE_WIDTH;

public class Quadratino extends BaseEntity<Quadratino> {

    private final int x, y;
    private final int width, height;
    private final Color color;

    private boolean hasMela;

    public Quadratino(int x, int y,
                      int width, int height,
                      Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onKeyPress(int keyCode) {
    }

    public void setMela(boolean mela) {
        hasMela = mela;
    }

    // Getters

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMiddleX() {
        return x + width / 2;
    }

    public int getMiddleY() {
        return y + height / 2;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMela() {
        return hasMela;
    }
}
