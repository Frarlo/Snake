package me.ferlo.snake;

import me.ferlo.neat.Config;
import me.ferlo.neat.Evolver;
import me.ferlo.snake.entity.EntityManager;
import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.swing.SwingRenderManager;
import me.ferlo.snake.util.SeiMortoException;
import me.ferlo.snake.util.Timer;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Snake implements Constants {

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

        Evolver.train(Config.newBuilder(6, 4, model -> {

            int oldScore = score;
            int sameScoreTicks = 0;

            int ticksPlayed = 0;
            int timeoutTicks = 0;

            try {
                while(true) {

                    if(++timeoutTicks > SQUARE_WIDTH / MOVEMENT_SPEED) {
                        timeoutTicks = 0;

                        ticksPlayed++;
                        if (score == oldScore)
                            sameScoreTicks++;
                        else
                            sameScoreTicks = 0;

                        if (sameScoreTicks > 200)
                            throw new SeiMortoException();

                        // Evaluate network

                        final Pitone pitone = entityManager.getCobra();
                        final Quadratino mela = entityManager.getMela();

                        float[] inputs = new float[6];

                        // Left distance
                        inputs[0] = pitone.getX();
                        for (int i = pitone.getHead().getMiddleX() - SQUARE_WIDTH; i > 0; i -= SQUARE_WIDTH) {
                            Quadratino quad = entityManager.getQuadrato(i, pitone.getHead().getMiddleY());
                            if (pitone.getSegmenti().contains(quad)) {
                                inputs[0] = pitone.getX() - quad.getMiddleX();
                                break;
                            }
                        }
                        // Right distance
                        inputs[1] = TABLE_WIDTH - pitone.getX();
                        for (int i = pitone.getHead().getMiddleX() + SQUARE_WIDTH; i < TABLE_WIDTH; i += SQUARE_WIDTH) {
                            Quadratino quad = entityManager.getQuadrato(i, pitone.getHead().getMiddleY());
                            if (pitone.getSegmenti().contains(quad)) {
                                inputs[1] = quad.getMiddleX() - pitone.getX();
                                break;
                            }
                        }
                        // Top distance
                        inputs[2] = pitone.getY();
                        for (int i = pitone.getHead().getMiddleY() - SQUARE_HEIGHT; i > 0; i -= SQUARE_HEIGHT) {
                            Quadratino quad = entityManager.getQuadrato(pitone.getHead().getMiddleX(), i);
                            if (pitone.getSegmenti().contains(quad)) {
                                inputs[2] = pitone.getY() - quad.getMiddleY();
                                break;
                            }
                        }
                        // Bottom distance
                        inputs[3] = TABLE_HEIGHT - pitone.getY();
                        for (int i = pitone.getHead().getMiddleY() + SQUARE_WIDTH; i < TABLE_HEIGHT; i += SQUARE_HEIGHT) {
                            Quadratino quad = entityManager.getQuadrato(pitone.getHead().getMiddleX(), i);
                            if (pitone.getSegmenti().contains(quad)) {
                                inputs[3] = quad.getMiddleY() - pitone.getY();
                                break;
                            }
                        }

                        inputs[4] = pitone.getX() - mela.getMiddleX(); // Mela x diff
                        inputs[5] = pitone.getY() - mela.getMiddleY(); // Mela y diff

                        float[] outputs = model.evaluate(inputs);

                        if (outputs[0] > 0.5f)
                            entityManager.onPressKey(KeyEvent.VK_LEFT);
                        else if (outputs[1] > 0.5f)
                            entityManager.onPressKey(KeyEvent.VK_RIGHT);
                        else if (outputs[2] > 0.5f)
                            entityManager.onPressKey(KeyEvent.VK_UP);
                        else if (outputs[3] > 0.5f)
                            entityManager.onPressKey(KeyEvent.VK_DOWN);
                        System.out.println(Arrays.toString(inputs) + " -> " + Arrays.toString(outputs));
                    }

                    // Ticks
                    entityManager.onTick();
                    // Graphics
                    renderer.render();
                }
            } catch (SeiMortoException ex) {
                restart();
            }

            final Pitone pitone = entityManager.getCobra();
            final Quadratino mela = entityManager.getMela();

            final float melaXdiff = Math.abs(pitone.getX() - mela.getMiddleX());
            final float melayDiff = Math.abs(pitone.getY() - mela.getMiddleY());

            if(ticksPlayed == 0)
                ticksPlayed++;

            float fitness = (float)score / (float)ticksPlayed;
//            if(melaXdiff != 0)
//                fitness += 1 / melaXdiff;
//            if(melayDiff != 0)
//                fitness += 1 / melayDiff;
            if(fitness == 0)
                fitness = ticksPlayed / 100f;


            System.out.println(fitness);

            return fitness;
        }));

//        try {
//            while(true) {
//                final long startMs = System.currentTimeMillis();
//
//                // Ticks
//
//                final long ticks = ticksTimer.getTimePassed() / (1000 / TPS);
//                for(int i = 0; i < ticks; i++)
//                    onTick();
//
//                if(ticks > 0)
//                    ticksTimer.reset();
//
//                // Graphics
//
//                renderer.render();
//
//                // Wait until the next frame
//
//                final long endMs = System.currentTimeMillis();
//                final long timePassed = endMs - startMs;
//                final long toWait = (1000 / FPS) - timePassed;
//
//                if(toWait > 0)
//                    Thread.sleep(toWait);
//            }
//        } catch (InterruptedException ex) {
//            System.err.println("Main thread unexpectedly interrupted");
//            ex.printStackTrace();
//        }
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

    public int getGeneration() {
        return Evolver.getGeneration();
    }
}
