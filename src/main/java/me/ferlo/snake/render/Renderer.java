package me.ferlo.snake.render;

public interface Renderer<T> {
    void onRender(RenderContext ctx, T toRender);
}
