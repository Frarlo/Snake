package me.ferlo.snake.entity;

import me.ferlo.snake.render.Renderable;

public interface Entity extends Renderable {
    void onTick();

    void onKeyPress(int keyCode);
}
