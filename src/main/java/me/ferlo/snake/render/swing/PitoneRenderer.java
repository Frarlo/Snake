package me.ferlo.snake.render.swing;

import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.util.MoveDirection;

import java.awt.*;

import static me.ferlo.snake.util.MoveDirection.*;

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

        if(dir == UP || dir == DOWN)
            yToDraw -= yDiff;
        else /* if(dir == MoveDirection.LEFT || dir == MoveDirection.RIGHT) */
            xToDraw -= xDiff;

        g2d.fillOval(xToDraw - 10, yToDraw - 10, 20, 20);

        if(toRender.getSegmenti().size() != 1) {
            switch(dir) {
                case UP: {
                    final int height = head.getY() + head.getHeight() - yToDraw;
                    g2d.fillRect(xToDraw - 10, yToDraw, 20, height);
                    break;
                }
                case DOWN:
                    final int height = yToDraw - head.getY();
                    g2d.fillRect(xToDraw - 10, head.getY(), 20, height);
                    break;
                case LEFT: {
                    final int width = head.getX() + head.getWidth() - xToDraw;
                    g2d.fillRect(xToDraw, yToDraw - 10, width, 20);
                    break;
                }
                case RIGHT:
                    final int width = xToDraw - head.getX();
                    g2d.fillRect(head.getX(), yToDraw - 10, width, 20);
                    break;
            }
            g2d.setColor(getRainbowColor());
        }

        // Body

        int lastCoordX = xToDraw;
        int lastCoordY = yToDraw;

        Quadratino lastQuad = null;
        for(int i = 0; i < toRender.getSegmenti().size(); i++) {

            final Quadratino newQuad = toRender.getSegmenti().get(i);
            final boolean isLast = i + 1 == toRender.getSegmenti().size();

            if(lastQuad == null) { // Skip the head
                lastQuad = newQuad;
                continue;
            }

            final MoveDirection quadDir = lastQuad.getDirection(newQuad);

            int middleX = newQuad.getMiddleX();
            int middleY = newQuad.getMiddleY();

            if(isLast) {
                if(quadDir == LEFT || quadDir == RIGHT)
                    middleX -= xDiff;
                else if(quadDir == UP || quadDir == DOWN)
                    middleY -= yDiff;
            }

            switch(quadDir) {
                case UP:
                    g2d.fillRect(
                            middleX - 10,
                            middleY - 10,
                            20,
                            lastCoordY - middleY + 10);
                    break;
                case DOWN:
                    g2d.fillRect(
                            middleX - 10,
                            lastCoordY,
                            20,
                            middleY - lastCoordY + 10);
                    break;
                case LEFT:
                    g2d.fillRect(
                            middleX - 10,
                            middleY - 10,
                            lastCoordX - middleX + 10,
                            20);
                    break;
                case RIGHT:
                    g2d.fillRect(
                            lastCoordX,
                            middleY - 10,
                            middleX - lastCoordX + 10,
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
