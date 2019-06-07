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
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onRender(SwingContext ctx, Graphics2D g2d, Pitone toRender) {

        g2d.setColor(getRainbowColor());

        // Head

        final Quadratino head = toRender.getHead();

        final MoveDirection movDir = toRender.getCurrDir();

        int xToDraw = head.getMiddleX();
        int yToDraw = head.getMiddleY();

        final int yDiff = head.getMiddleY() - toRender.getY();
        final int xDiff = head.getMiddleX() - toRender.getX();

        if(movDir.isVertical())
            yToDraw -= yDiff;
        else // if (movDir.isHorizontal())
            xToDraw -= xDiff;

        if(toRender.getSegmenti().size() != 1) {

            // Draw from the head position till the end of the square
            // so corners gets drawn correctly

            switch(movDir) {
                case UP: {
                    final int height = head.getMiddleY() + SNAKE_WIDTH / 2 - yToDraw;
                    ctx.drawBorderedRect(xToDraw - SNAKE_WIDTH / 2, yToDraw, SNAKE_WIDTH, height, Color.black);
                    break;
                }
                case DOWN:
                    final int startY = head.getMiddleY() - SNAKE_WIDTH / 2;
                    final int height = yToDraw - startY;
                    ctx.drawBorderedRect(xToDraw - SNAKE_WIDTH / 2, startY, SNAKE_WIDTH, height, Color.black);
                    break;
                case LEFT: {
                    final int width = head.getMiddleX() + SNAKE_WIDTH / 2 - xToDraw;
                    ctx.drawBorderedRect(xToDraw, yToDraw - SNAKE_WIDTH / 2, width, SNAKE_WIDTH, Color.black);
                    break;
                }
                case RIGHT:
                    final int startX = head.getMiddleX() - SNAKE_WIDTH / 2;
                    final int width = xToDraw - startX;
                    ctx.drawBorderedRect(startX, yToDraw - SNAKE_WIDTH / 2, width, SNAKE_WIDTH, Color.black);
                    break;
            }
        }

        ctx.drawBorderedCircle(
                xToDraw - SNAKE_WIDTH / 2,
                yToDraw - SNAKE_WIDTH / 2, SNAKE_WIDTH,
                Color.black);

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
                final MoveDirection tailMovDir = quadDir.getOpposto();
                int diff = movDir.isVertical() ? yDiff : xDiff;

                // If the tail is moving in an opposite direction compared to the head,
                // revert the difference sign
                if(shouldFixDiff(movDir, tailMovDir))
                    diff *= -1;

                if(tailMovDir.isVertical())
                    middleY -= diff;
                else // if(tailMovDir.isHorizontal())
                    middleX -= diff;
            }

            switch(quadDir) {
                case UP:
                    ctx.drawBorderedRect(
                            middleX - SNAKE_WIDTH / 2,
                            middleY - SNAKE_WIDTH / 2,
                            SNAKE_WIDTH,
                            lastCoordY - middleY + SNAKE_WIDTH / 2,
                            Color.black);
                    break;
                case DOWN:
                    ctx.drawBorderedRect(
                            middleX - SNAKE_WIDTH / 2,
                            lastCoordY,
                            SNAKE_WIDTH,
                            middleY - lastCoordY + SNAKE_WIDTH / 2,
                            Color.black);
                    break;
                case LEFT:
                    ctx.drawBorderedRect(
                            middleX - SNAKE_WIDTH / 2,
                            middleY - SNAKE_WIDTH / 2,
                            lastCoordX - middleX + SNAKE_WIDTH / 2,
                            SNAKE_WIDTH,
                            Color.black);
                    break;
                case RIGHT:
                    ctx.drawBorderedRect(
                            lastCoordX,
                            middleY - SNAKE_WIDTH / 2,
                            middleX - lastCoordX + SNAKE_WIDTH / 2,
                            SNAKE_WIDTH,
                            Color.black);
                    break;
            }

            lastQuad = newQuad;

            lastCoordX = newQuad.getMiddleX();
            lastCoordY = newQuad.getMiddleY();
        }
    }

    private boolean shouldFixDiff(MoveDirection headMovDir,
                                  MoveDirection tailMovDir) {
        return shouldFixDiff0(headMovDir, tailMovDir) || shouldFixDiff0(tailMovDir, headMovDir);
    }

    private boolean shouldFixDiff0(MoveDirection dir1,
                                   MoveDirection dir2) {
        return dir1.isOpposto(dir2) ||
                (dir1 == UP && dir2 == RIGHT) ||
                (dir1 == DOWN && dir2 == LEFT);
    }

    private static Color getRainbowColor() {
        return Color.getHSBColor(System.nanoTime() / 10000000000f, 1, 0.95f);
    }
}
