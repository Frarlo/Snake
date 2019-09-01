package me.ferlo.snake.entity;

public interface Entity {
    void onTick();

    void onKeyPress(int keyCode);
}
