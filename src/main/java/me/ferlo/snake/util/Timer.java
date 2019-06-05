package me.ferlo.snake.util;

public class Timer {

    private long lastMillis;

    public Timer() {
        reset();
    }

    public void reset() {
        lastMillis = getCurrentMillis();
    }

    public boolean hasTimePassed(long millis) {
        return getTimePassed() >= millis;
    }

    public long getTimePassed() {
        return getCurrentMillis() - lastMillis;
    }

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
