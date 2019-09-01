package me.ferlo.snake.render;

public abstract class BaseRenderer<T, R extends RenderManager> implements Renderer<T> {

    protected final R renderManager;
    protected int priority;

    public BaseRenderer(R renderManager) {
        this(renderManager, NORMAL_PRIORITY);
    }

    protected BaseRenderer(R renderManager, int priority) {
        this.renderManager = renderManager;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
