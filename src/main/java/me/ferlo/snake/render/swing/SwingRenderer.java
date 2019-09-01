package me.ferlo.snake.render.swing;

import me.ferlo.snake.render.BaseRenderer;
import me.ferlo.snake.render.RenderContext;

import java.awt.*;

public abstract class SwingRenderer<T> extends BaseRenderer<T, SwingRenderManager> {

    public SwingRenderer(SwingRenderManager renderManager) {
        super(renderManager);
    }

    public SwingRenderer(SwingRenderManager renderManager, int priority) {
        super(renderManager, priority);
    }

    @Override
    public void onRender(RenderContext ctx, T toRender) {
        if(!(ctx instanceof SwingContext))
            throw new UnsupportedOperationException("Wrong render context, needed SwingContext");
        onRender((SwingContext) ctx, ((SwingContext) ctx).getGraphics(), toRender);
    }

    protected abstract void onRender(SwingContext ctx, Graphics2D g2d, T toRender);
}
