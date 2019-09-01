package me.ferlo.snake.render.swing;

import me.ferlo.snake.Constants;
import me.ferlo.snake.Game;
import me.ferlo.snake.entity.Entity;
import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.Renderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SwingRenderManager extends JPanel implements RenderManager, Constants {

    // Constants

    private static final File BASE_PATH;
    private static final boolean SAVE_FRAMES = false;

    static {

        File file;
        int i = 0;
        do {
            file = new File("C:" + File.separator + "tests" + File.separator + "test_" + (i++));
        } while (file.exists());

        BASE_PATH = file;
        BASE_PATH.mkdirs();
    }

    // Attributes

    private final Game game;
    private final JFrame frame;

    private final Map<Class<?>, Renderer<?>> rendererMap;

    private final ExecutorService executor;
    private int frameNumber;

    private final Semaphore semaphore = new Semaphore(0);

    public SwingRenderManager(Game game) {
        this.game = game;

        rendererMap = new HashMap<>();
        rendererMap.put(Pitone.class, new PitoneRenderer(this));
        rendererMap.put(Quadratino.class, new QuadratinoRenderer(this));

        executor = Executors.newSingleThreadExecutor();

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
    public void paint(Graphics g) {
        super.paint(g);

        if(SAVE_FRAMES) {
            try {
                BufferedImage imgBuf = new Robot().createScreenCapture(bounds());
                Graphics2D graphics2D = imgBuf.createGraphics();
                super.paint(graphics2D);

                final int currFrameNumber = frameNumber++;
                executor.submit(() -> ImageIO.write(imgBuf, "jpeg",
                        new File(BASE_PATH, "capture_" + currFrameNumber + ".jpeg")));
            } catch (Exception e1) {
                System.err.println("Couldn't save frame");
                e1.printStackTrace();
            }
        }
        semaphore.release();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        final SwingContext ctx = new SwingContext(g2d);

        final int startX = (getWidth() - TABLE_WIDTH) / 2;
        final int startY = (getHeight() - TABLE_HEIGHT) / 2;

        g.translate(startX, startY);
        g.drawRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);


        //noinspection unchecked
        game.getEntityManager().getEntities()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e, (Renderer<Entity>) getRendererFor(e.getClass())))
                .sorted(Comparator.comparingInt(e -> e.getValue().getPriority()))
                .forEach(entry -> entry.getValue().onRender(ctx, entry.getKey()));

        g.translate(-startX, -startY);

        ctx.drawCenteredString(
                "Punteggio: " + game.getScore(),
                getWidth() / 2,
                getHeight() - 20);
    }

    @Override
    public void render() {
        if(!frame.isVisible())
            frame.setVisible(true);

        semaphore.drainPermits();
        repaint();
        semaphore.acquireUninterruptibly();
    }

    @Override
    public void showDialog(String title, String message,
                           String yesButton, String noButton,
                           Runnable onYes, Runnable onNo) {

        final int response = JOptionPane.showOptionDialog(frame,
                message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[] { yesButton, noButton }, yesButton);

        if(response == 0) {
            if(onYes != null)
                onYes.run();
        } else {
            if(onNo != null)
                onNo.run();
        }
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
