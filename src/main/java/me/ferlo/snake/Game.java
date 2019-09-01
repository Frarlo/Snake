package me.ferlo.snake;

import me.ferlo.snake.entity.EntityManager;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.swing.SwingRenderManager;
import me.ferlo.snake.util.SeiMortoException;
import me.ferlo.snake.util.Timer;

import java.util.function.Function;

public class Game implements Constants {

    // Squadrette

    protected Function<Game, RenderManager> rendererFactory;

    protected boolean started = false;

    protected RenderManager renderer;
    protected EntityManager entityManager;
    protected Timer ticksTimer;

    protected int score;

    public Game(Function<Game, RenderManager> rendererFactory) {
        this.rendererFactory = rendererFactory;
    }

    public Game() {
        this(SwingRenderManager::new);
    }

    public void start() {
        if(started)
            throw new UnsupportedOperationException("Cannot start a game twice :thinking:");
        started = true;

        renderer = rendererFactory.apply(this);
        rendererFactory = null;

        entityManager = new EntityManager(this);
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

    protected void restart() {
        entityManager.restart();
        score = 0;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void gameLoop() {
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
