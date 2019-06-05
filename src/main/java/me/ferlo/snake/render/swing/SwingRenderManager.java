package me.ferlo.snake.render.swing;

import me.ferlo.snake.Constants;
import me.ferlo.snake.Snake;
import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class SwingRenderManager extends JPanel implements RenderManager, Constants {

    // Constants

    private static final Snake game = Snake.getInstance();

    // Attributes

    private final JFrame frame;

    private final Map<Class<?>, Renderer<?>> rendererMap;

    public SwingRenderManager() {

        rendererMap = new HashMap<>();
        rendererMap.put(Pitone.class, new PitoneRenderer(this));
        rendererMap.put(Quadratino.class, new QuadratinoRenderer(this));

        frame = new JFrame() {
            @Override
            public void dispose() {
                game.shutdown();
            }
        };
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setResizable(false);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);

        frame.addKeyListener(new KeyboardHandler());

        frame.setContentPane(this);
    }

    @Override
    public void paintComponent(Graphics g) {

        final long startMs = System.currentTimeMillis();

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        final SwingContext ctx = new SwingContext(g2d);

        final int startX = (getWidth() - TABLE_WIDTH) / 2;
        final int startY = (getHeight() - TABLE_HEIGHT) / 2;

        g.translate(startX, startY);
        g.drawRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
        game.getEntityManager().onRender(ctx);
        g.translate(-startX, -startY);

        ctx.drawCenteredString(
                "Punteggio: " + Snake.getInstance().getScore(),
                getWidth() / 2,
                getHeight() - 20);

        // Wait until the next frame

        final long endMs = System.currentTimeMillis();
        final long timePassed = endMs - startMs;
        final long toWait = (1000 / FPS) - timePassed;

        if(toWait > 0)
            repaint(toWait);
        else
            repaint();
    }

    @Override
    public void startRendering() {
        frame.setVisible(true);
    }

    @Override
    public void showDialog(String title, String message,
                           String yesButton, String noButton,
                           Runnable onYes, Runnable onNo) {

        final int response = JOptionPane.showOptionDialog(frame,
                message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[] { yesButton, noButton }, yesButton);

        if(response == 0)
            onYes.run();
        else
            onNo.run();
    }

    private final class KeyboardHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent ke) {
            game.getEntityManager().onPressKey(ke.getKeyCode());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Renderer<T> getRendererFor(Class<T> type) {
        return (Renderer<T>) rendererMap.get(type);
    }
}
