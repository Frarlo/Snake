package me.ferlo.snake.render.swing;

import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.util.MoveDirection;

import java.awt.*;

public class PitoneRenderer extends SwingRenderer<Pitone> {

    public PitoneRenderer(SwingRenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void onRender(SwingContext ctx, Graphics2D g2d, Pitone toRender) {

        g2d.setColor(getRainbowColor());

        // Head

        final Quadratino head = toRender.getHead();
        final MoveDirection dir = toRender.getCurrDir();

        int xToDraw = head.getMiddleX();
        int yToDraw = head.getMiddleY();

        final int yDiff = head.getMiddleY() - toRender.getY();
        final int xDiff = head.getMiddleX() - toRender.getX();

        if(dir == MoveDirection.UP || dir == MoveDirection.DOWN)
            yToDraw -= yDiff;
        else /* if(dir == MoveDirection.LEFT || dir == MoveDirection.RIGHT) */
            xToDraw -= xDiff;

        g2d.fillOval(xToDraw - 10, yToDraw - 10, 20, 20);

        // Body

        int lastCoordX = xToDraw;
        int lastCoordY = yToDraw;

        Quadratino lastQuad = null;
        for(int i = 0, len = toRender.getSegmenti().size(); i < len; i++) {

            final Quadratino newQuad = toRender.getSegmenti().get(i);
            final boolean isLast = i + 1 == len;

            if(lastQuad == null) { // Skip the head
                lastQuad = newQuad;
                continue;
            }

            switch(lastQuad.getDirection(newQuad)) {
                case UP:
                    int startY = newQuad.getMiddleY();
                    if(isLast)
                        startY -= yDiff;

                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            startY - 10,
                            20,
                            lastCoordY - startY + 10);
                    break;
                case DOWN:
                    int height = newQuad.getMiddleY() - lastCoordY + 10;
                    if(isLast)
                        height -= yDiff;

                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            lastCoordY,
                            20,
                            height);
                    break;
                case RIGHT:
                    g2d.fillRect(
                            lastCoordX,
                            newQuad.getMiddleY() - 10,
                            newQuad.getMiddleX() - lastCoordX + 10,
                            20);
                    break;
                case LEFT:
                    g2d.fillRect(
                            newQuad.getMiddleX() - 10,
                            newQuad.getMiddleY() - 10,
                            lastCoordX - newQuad.getMiddleX() + 10,
                            20);
                    break;
            }

            lastQuad = newQuad;

            lastCoordX = newQuad.getMiddleX();
            lastCoordY = newQuad.getMiddleY();
        }
    }

    private static Color getRainbowColor() {
        return Color.getHSBColor(System.nanoTime() / 10000000000f, 1, 0.95f);
    }
}
