package me.ferlo.snake;

import me.ferlo.neat.Config;
import me.ferlo.neat.Evolver;
import me.ferlo.neat.FitnessCalculator;
import me.ferlo.neat.Model;
import me.ferlo.snake.entity.EntityManager;
import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.swing.SwingRenderManager;
import me.ferlo.snake.util.SeiMortoException;
import me.ferlo.snake.util.Timer;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

public class Snake implements Constants {

    // Constants

    private static Snake INSTANCE;

    // Squadrette

    private boolean started = false;

    private RenderManager renderer;
    private EntityManager entityManager;
    private Timer ticksTimer;

    private int score;

    public boolean skipSleep = false;
    
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

        final FitnessCalculator fitnessCalculator = (model, fitnessConsumer) -> {

            final List<Integer> keys = new ArrayList<>();
            keys.add(KeyEvent.VK_LEFT);
            keys.add(KeyEvent.VK_RIGHT);
            keys.add(KeyEvent.VK_UP);
            keys.add(KeyEvent.VK_DOWN);

            int oldScore = score;
            int sameScoreTicks = 0;
            int timeoutTicks = 0;

            // Old fitness func
            final int[] ticksPlayed = { 0 };
            final DoubleSupplier fitnessSupplier = () -> score * 100f / ticksPlayed[0];

            // New fitness func
            final float[] fitness = { 0 };
            int prevAppleXDist = Math.abs(entityManager.getCobra().getHead().getMiddleX() - entityManager.getMela().getMiddleX());
            int prevAppleYDist = Math.abs(entityManager.getCobra().getHead().getMiddleY() - entityManager.getMela().getMiddleY());
//            final DoubleSupplier fitnessSupplier = () -> fitness[0] + 100 * score;

            try {
                while (true) {
                    final long startMs = System.currentTimeMillis();

                    if (++timeoutTicks > SQUARE_WIDTH / MOVEMENT_SPEED) {
                        timeoutTicks = 0;

                        final Pitone pitone = entityManager.getCobra();
                        final Quadratino apple = entityManager.getMela();

                        // Kill loops

                        ticksPlayed[0]++;
                        if (score == oldScore)
                            sameScoreTicks++;
                        else
                            sameScoreTicks = 0;
                        oldScore = score;

                        if (sameScoreTicks > 100 * pitone.getSegmenti().size())
                            throw new SeiMortoException();

                        // Reward going towards the apple

                        final int appleXDist = Math.abs(pitone.getHead().getMiddleX() - apple.getMiddleX());
                        final int appleYDist = Math.abs(pitone.getHead().getMiddleY() - apple.getMiddleY());

                        if(appleXDist != prevAppleXDist)
                            fitness[0] += appleXDist < prevAppleXDist ? 1f : -1.5f;
                        if(appleYDist != prevAppleYDist)
                            fitness[0] += appleYDist < prevAppleYDist ? 1f : -1.5f;
                        prevAppleXDist = appleXDist;
                        prevAppleYDist = appleYDist;

                        // Evaluate network

                        float[] inputs = new float[6];

                        // Left
                        final int prevX = pitone.getHead().getMiddleX() - SQUARE_WIDTH;
                        if(prevX < 0)
                            inputs[0] = 1f;
                        else {
                            final Quadratino quad = entityManager.getQuadrato(prevX, pitone.getHead().getMiddleY());
                            if(pitone.getSegmenti().contains(quad))
                                inputs[0] = 1f;
                        }
                        // Right
                        final int nextX = pitone.getHead().getMiddleX() + SQUARE_WIDTH;
                        if(nextX > TABLE_WIDTH)
                            inputs[1] = 1f;
                        else {
                            final Quadratino quad = entityManager.getQuadrato(nextX, pitone.getHead().getMiddleY());
                            if(pitone.getSegmenti().contains(quad))
                                inputs[1] = 1f;
                        }
                        // Up
                        final int prevY = pitone.getHead().getMiddleY() - SQUARE_HEIGHT;
                        if(prevY < 0)
                            inputs[2] = 1f;
                        else {
                            final Quadratino quad = entityManager.getQuadrato(pitone.getHead().getMiddleX(), prevY);
                            if(pitone.getSegmenti().contains(quad))
                                inputs[2] = 1f;
                        }
                        // Down
                        final int nextY = pitone.getHead().getMiddleY() + SQUARE_HEIGHT;
                        if(nextY > TABLE_HEIGHT)
                            inputs[3] = 1f;
                        else {
                            final Quadratino quad = entityManager.getQuadrato(pitone.getHead().getMiddleX(), nextY);
                            if(pitone.getSegmenti().contains(quad))
                                inputs[3] = 1f;
                        }

                        inputs[4] = pitone.getHead().getMiddleX() > apple.getMiddleX() ? -1f :
                                pitone.getHead().getMiddleX() < apple.getMiddleX() ? 1f : 0f;
                        inputs[5] = pitone.getHead().getMiddleY() > apple.getMiddleY() ? -1f :
                                pitone.getHead().getMiddleY() < apple.getMiddleY() ? 1f : 0f;

                        float[] outputs = model.evaluate(inputs);

                        final List<Map.Entry<Integer, Float>> sortedOutputs = new ArrayList<>();
                        IntStream.range(0, outputs.length)
                                .forEach(i -> sortedOutputs.add(new AbstractMap.SimpleEntry<>(i, outputs[i])));
                        sortedOutputs.stream()
                                .filter(e -> e.getValue() >= 0.5F)
                                .sorted(Comparator.comparingDouble((ToDoubleFunction<Map.Entry<Integer, Float>>) Map.Entry::getValue).reversed())
                                .forEachOrdered(e -> entityManager.onPressKey(keys.get(e.getKey())));

                        System.out.println(Arrays.toString(inputs) + " -> " + Arrays.toString(outputs));
                        fitnessConsumer.accept((float) fitnessSupplier.getAsDouble());
                    }

                    // Ticks
                    entityManager.onTick();
                    // Graphics
                    renderer.render();

                    // Wait until the next frame

                    final long endMs = System.currentTimeMillis();
                    final long timePassed = endMs - startMs;
                    final long toWait = (1000 / FPS) - timePassed;

                    if(!skipSleep && toWait > 0) {
                        try {
                            Thread.sleep(toWait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SeiMortoException ex) {
                fitnessConsumer.accept((float) fitnessSupplier.getAsDouble());
                restart();
            }
        };

//        Mythan.newInstance(6, 4, new CustomizedSigmoidActivation(), new FitnessCalculator() {
//            @Override
//            public double getFitness(Network network) {
        final Model best = Evolver.train(Config.newBuilder(6, 4, fitnessCalculator)
                .setTargetGeneration(100));
        skipSleep = false;
        while(true)
            fitnessCalculator.calculateFitness(best, fitness -> {});


//        }).trainToFitness(150, Double.MAX_VALUE);

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
}
