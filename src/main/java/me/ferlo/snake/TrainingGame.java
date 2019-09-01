package me.ferlo.snake;

import me.ferlo.neat.Model;
import me.ferlo.snake.entity.Pitone;
import me.ferlo.snake.entity.Quadratino;
import me.ferlo.snake.render.RenderManager;
import me.ferlo.snake.render.Renderer;
import me.ferlo.snake.util.SeiMortoException;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

public class TrainingGame extends Game {

    private static final List<Integer> KEYS = new ArrayList<>();

    static {
        KEYS.add(KeyEvent.VK_LEFT);
        KEYS.add(KeyEvent.VK_RIGHT);
        KEYS.add(KeyEvent.VK_UP);
        KEYS.add(KeyEvent.VK_DOWN);
    }

    protected Model model;
    protected final Consumer<Float> fitnessConsumer;
    protected boolean skipSleep;

    public TrainingGame(Model model, Consumer<Float> fitnessConsumer) {
        super((game) -> new RenderManager() {
            @Override
            public void render() {
            }

            @Override
            public void showDialog(String title, String message,
                                   String yesButton, String noButton,
                                   Runnable onYes, Runnable onNo) {
                onYes.run();
            }

            @Override
            public <T> Renderer<T> getRendererFor(Class<T> type) {
                return null;
            }
        });

        this.model = model;
        this.fitnessConsumer = fitnessConsumer;
        this.skipSleep = true;
    }

    @Override
    protected void gameLoop() {
        int oldScore = score;
        int sameScoreTicks = 0;

        // Old fitness func
        final int[] ticksPlayed = { 0 };
        final DoubleSupplier fitnessSupplier = () -> score * score / (float)ticksPlayed[0];

        // New fitness func
        final float[] fitness = { 0 };
        int prevAppleXDist = Math.abs(entityManager.getCobra().getHead().getMiddleX() - entityManager.getMela().getMiddleX());
        int prevAppleYDist = Math.abs(entityManager.getCobra().getHead().getMiddleY() - entityManager.getMela().getMiddleY());
//      final DoubleSupplier fitnessSupplier = () -> fitness[0] + 100 * score;

        try {
            while (true) {
                final long startMs = System.currentTimeMillis();

                if (entityManager.getCobra().canChangeDir()) {

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
                            .forEachOrdered(e -> entityManager.onPressKey(KEYS.get(e.getKey())));

                    System.out.println(Arrays.toString(inputs) + " -> " + Arrays.toString(outputs));
                    fitnessConsumer.accept((float) fitnessSupplier.getAsDouble());
                }

                // Ticks
                entityManager.onTick();
                // Renderer
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
        }
    }
}
