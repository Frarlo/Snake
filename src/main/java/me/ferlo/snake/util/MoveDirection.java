package me.ferlo.snake.util;

public enum MoveDirection {
    UP, DOWN, RIGHT, LEFT;

    public MoveDirection getOpposto() {
        switch(this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
        }
        throw new RuntimeException("Come sei arrivato qua?"); //Unreachable
    }

    public boolean isOpposto(MoveDirection dir) {
        return getOpposto().equals(dir);
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    public boolean isHorizontal() {
        return this == UP || this == DOWN;
    }
}
