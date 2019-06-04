package me.ferlo.snake.render;

public interface Renderable {
    int HIGH_PRIORITY = 1;
    int NORMAL_PRIORITY = 0;
    int LOW_PRIORITY = -1;
    
    void onRender(RenderContext ctx);

    int getPriority();
}
