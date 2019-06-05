package me.ferlo.snake;

import me.ferlo.snake.entity.EntityManager;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.swing.SwingRenderManager;
import me.ferlo.snake.util.SeiMortoException;
import me.ferlo.snake.util.Timer;

public final class Snake implements Constants {

    // Constants

    private static Snake INSTANCE;

    // Squadrette

    private boolean started = false;

    private RenderManager renderer;
    private EntityManager entityManager;
    private Timer ticksTimer;

    private int score;
    
    private Snake() {
    }
    
    public static Snake getInstance() {
        if(INSTANCE == null)
            INSTANCE = new Snake();
        return INSTANCE;
    }
    
    public void start() {
        if(started)
            throw new UnsupportedOperationException("Cannot start a game twice :thinking:");
        started = true;

        renderer = new SwingRenderManager();
        entityManager = new EntityManager();
        ticksTimer = new Timer();
        restart();

        renderer.render();
        renderer.showDialog(
                "Start Game", "Press start to start",
                "Start", "Exit",
                () -> {
                    ticksTimer.reset();
                    gameLoop();
                }, this::shutdown);
    }

    private void restart() {
        entityManager.restart();
        score = 0;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void gameLoop() {
        try {
            while(true) {
                final long startMs = System.currentTimeMillis();

                // Ticks

                final long ticks = ticksTimer.getTimePassed() / (1000 / TPS);
                for(int i = 0; i < ticks; i++)
                    onTick();

                if(ticks > 0)
                    ticksTimer.reset();

                // Graphics

                renderer.render();

                // Wait until the next frame

                final long endMs = System.currentTimeMillis();
                final long timePassed = endMs - startMs;
                final long toWait = (1000 / FPS) - timePassed;

                if(toWait > 0)
                    Thread.sleep(toWait);
            }
        } catch (InterruptedException ex) {
            System.err.println("Main thread unexpectedly interrupted");
            ex.printStackTrace();
        }
    }

    private void onTick() {
        try {
            entityManager.onTick();
        } catch (SeiMortoException ex) {
            renderer.showDialog(
                    "RIP", "Sei mmmmorto !!!",
                    "Restart", "Exit",
                    () -> {
                        restart();
                        ticksTimer.reset();
                    }, this::shutdown);
        }
    }

    public void shutdown() {
        System.exit(0);
    }

    public void melaMangiata() {
        score++;
        entityManager.spawnMela();
    }

    // Getters

    public RenderManager getRenderManager() {
        return renderer;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public int getScore() {
        return score;
    }
}
