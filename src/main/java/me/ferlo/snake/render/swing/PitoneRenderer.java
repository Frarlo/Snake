package me.ferlo.snake.render.swing;

import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.util.MoveDirection;

import java.awt.*;

import static me.ferlo.snake.util.MoveDirection.*;

public class PitoneRenderer extends SwingRenderer<Pitone> {

    private static final int SNAKE_WIDTH = 10;

    public PitoneRenderer(SwingRenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void onRender(SwingContext ctx, Graphics2D g2d, Pitone toRender) {

        g2d.setColor(getRainbowColor());

        // Head

        final Quadratino head = toRender.getHead();

        final MoveDirection movDir = toRender.getCurrDir();
        final boolean isMovVertical = movDir == UP || movDir == DOWN;

        int xToDraw = head.getMiddleX();
        int yToDraw = head.getMiddleY();

        final int yDiff = head.getMiddleY() - toRender.getY();
        final int xDiff = head.getMiddleX() - toRender.getX();

        if(isMovVertical)
            yToDraw -= yDiff;
        else
            xToDraw -= xDiff;

        g2d.fillOval(
                xToDraw - SNAKE_WIDTH / 2,
                yToDraw - SNAKE_WIDTH / 2, SNAKE_WIDTH, SNAKE_WIDTH);

        if(toRender.getSegmenti().size() != 1) {
            switch(movDir) {
                case UP: {
                    final int height = head.getMiddleY() + SNAKE_WIDTH / 2 - yToDraw;
                    g2d.fillRect(xToDraw - SNAKE_WIDTH / 2, yToDraw, SNAKE_WIDTH, height);
                    break;
                }
                case DOWN:
                    final int startY = head.getMiddleY() - SNAKE_WIDTH / 2;
                    final int height = yToDraw - startY;
                    g2d.fillRect(xToDraw - SNAKE_WIDTH / 2, startY, SNAKE_WIDTH, height);
                    break;
                case LEFT: {
                    final int width = head.getMiddleX() + SNAKE_WIDTH / 2 - xToDraw;
                    g2d.fillRect(xToDraw, yToDraw - SNAKE_WIDTH / 2, width, SNAKE_WIDTH);
                    break;
                }
                case RIGHT:
                    final int startX = head.getMiddleX() - SNAKE_WIDTH / 2;
                    final int width = xToDraw - startX;
                    g2d.fillRect(startX, yToDraw - SNAKE_WIDTH / 2, width, SNAKE_WIDTH);
                    break;
            }
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
                final int diff = isMovVertical ? yDiff : xDiff;
                if(quadDir == LEFT)
                    middleX -= diff;
                else if(quadDir == UP || quadDir == DOWN)
                    middleY -= diff;
            }

            switch(quadDir) {
                case UP:
                    g2d.fillRect(
                            middleX - SNAKE_WIDTH / 2,
                            middleY - SNAKE_WIDTH / 2,
                            SNAKE_WIDTH,
                            lastCoordY - middleY + SNAKE_WIDTH / 2);
                    break;
                case DOWN:
                    g2d.fillRect(
                            middleX - SNAKE_WIDTH / 2,
                            lastCoordY,
                            SNAKE_WIDTH,
                            middleY - lastCoordY + SNAKE_WIDTH / 2);
                    break;
                case LEFT:
                    g2d.fillRect(
                            middleX - SNAKE_WIDTH / 2,
                            middleY - SNAKE_WIDTH / 2,
                            lastCoordX - middleX + SNAKE_WIDTH / 2,
                            SNAKE_WIDTH);
                    break;
                case RIGHT:
                    g2d.fillRect(
                            lastCoordX,
                            middleY - SNAKE_WIDTH / 2,
                            middleX - lastCoordX + SNAKE_WIDTH / 2,
                            SNAKE_WIDTH);
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
