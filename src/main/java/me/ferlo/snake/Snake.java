package me.ferlo.snake;

import me.ferlo.snake.entity.EntityManager;
import me.ferlo.snake.render.swing.SwingRenderManager;
import me.ferlo.snake.util.SeiMortoException;

public final class Snake implements Constants {

    // Constants

    private static Snake INSTANCE;

    // Squadrette

    private boolean started = false;

    private SwingRenderManager renderer;
    private EntityManager entityManager;

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

        restart();
        renderer.showDialog(
                "Start Game", "Press start to start",
                "Start", "Exit",
                this::gameLoop, this::shutdown);
    }

    private void restart() {
        entityManager.restart();
        score = 0;
    }
    
    @SuppressWarnings("InfiniteLoopStatement")
    private void gameLoop() {
        try {
            while (true) {
                entityManager.onTick();
                Thread.sleep(1000 / TPS);
            }
        } catch (InterruptedException ie) {
            System.err.println("Main thread unexpectedly interrupted");
            ie.printStackTrace();
        } catch (SeiMortoException ex) {
            renderer.showDialog(
                    "RIP", "Sei mmmmorto !!!",
                    "Restart", "Exit",
                    () -> {
                        restart();
                        gameLoop();
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


    public SwingRenderManager getRenderManager() {
        return renderer;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public int getScore() {
        return score;
    }
}
