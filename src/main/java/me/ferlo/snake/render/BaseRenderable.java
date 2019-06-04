package me.ferlo.snake.render;

import me.ferlo.snake.Snake;

public class BaseRenderable<T extends BaseRenderable> implements Renderable {

    // Constants

    protected static final Snake game = Snake.getInstance();

    // Attributes

    protected Renderer<T> renderer;
    protected int priority;

    protected BaseRenderable() {
        this(NORMAL_PRIORITY);
    }

    @SuppressWarnings("unchecked")
    protected BaseRenderable(int priority) {
        this.priority = priority;
        this.renderer = (Renderer<T>) game.getRenderManager().getRendererFor(getClass());
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onRender(RenderContext ctx) {
        renderer.onRender(ctx, (T) this);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
