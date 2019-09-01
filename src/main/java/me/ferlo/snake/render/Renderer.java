package me.ferlo.snake.render;

public interface Renderer<T> {
    int HIGH_PRIORITY = 1;
    int NORMAL_PRIORITY = 0;
    int LOW_PRIORITY = -1;

    void onRender(RenderContext ctx, T toRender);

    int getPriority();
}
