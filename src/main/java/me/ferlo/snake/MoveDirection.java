package me.ferlo.snake;

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
}
