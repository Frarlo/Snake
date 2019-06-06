package me.ferlo.snake.render.swing;

import me.ferlo.snake.entity.Quadratino;

import java.awt.*;

public class QuadratinoRenderer extends SwingRenderer<Quadratino> {

    public QuadratinoRenderer(SwingRenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void onRender(SwingContext ctx, Graphics2D g2d, Quadratino toRender) {

//        g2d.setColor(toRender.getColor());
//        g2d.fillRect(toRender.getX(), toRender.getY(), toRender.getWidth(), toRender.getHeight());
        g2d.setColor(Color.black);
        g2d.drawRect(toRender.getX(), toRender.getY(), toRender.getWidth(), toRender.getHeight());

        if(toRender.hasMela()) {
            g2d.setColor(Color.red);
            g2d.fillOval(
                    toRender.getMiddleX() - 6,
                    toRender.getMiddleY() - 6,
                    12, 12);
        }
    }
}
