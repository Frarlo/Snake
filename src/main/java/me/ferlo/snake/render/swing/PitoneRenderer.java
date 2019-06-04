package me.ferlo.snake.render.swing;

import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;

import java.awt.*;

public class PitoneRenderer extends SwingRenderer<Pitone> {

    public PitoneRenderer(SwingRenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void onRender(SwingContext ctx, Graphics2D g2d, Pitone toRender) {

        g2d.setColor(getRainbowColor());

        // Head
        Quadratino head = toRender.getHead();
        g2d.fillOval(
                head.getMiddleX() - 10,
                head.getMiddleY() - 10, 20, 20);

        Quadratino lastQuad = null;
        for(Quadratino newQuad : toRender.getSegmenti()) {

            if(lastQuad == null) { // Skip the head
                lastQuad = newQuad;
                continue;
            }

            switch(lastQuad.getDirection(newQuad)) {
                case UP:
                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            newQuad.getMiddleY() - 10,
                            20,
                            lastQuad.getMiddleY() - newQuad.getMiddleY() + 10);
                    break;
                case DOWN:
                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            lastQuad.getMiddleY(),
                            20,
                            newQuad.getMiddleY() - lastQuad.getMiddleY() + 10);
                    break;
                case RIGHT:
                    g2d.fillRect(
                            lastQuad.getMiddleX(),
                            newQuad.getMiddleY() - 10,
                            newQuad.getMiddleX() - lastQuad.getMiddleX() + 10,
                            20);
                    break;
                case LEFT:
                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            newQuad.getMiddleY() - 10,
                            lastQuad.getMiddleX() - newQuad.getMiddleX() + 10,
                            20);
                    break;
            }

            lastQuad = newQuad;
        }
    }

    private static Color getRainbowColor() {
        return Color.getHSBColor(System.nanoTime() / 10000000000f, 1, 0.95f);
    }
}
