package me.ferlo.snake.entity;

import me.ferlo.snake.Constants;
import me.ferlo.snake.render.RenderContext;
import me.ferlo.snake.render.Renderable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class EntityManager implements Constants {

    private final Quadratino[][] squadrette;

    private final Pitone cobra;
    private Quadratino mela;

    private final List<Entity> entities;

    private final Random melaRandom = new Random();

    public EntityManager() {
        final Color color1 = Color.green;
        final Color color2 = new Color(50, 147, 60);

        entities = new ArrayList<>();

        // Serpente

        cobra = new Pitone();
        entities.add(cobra);

        // Quadratino

        squadrette = new Quadratino[TABLE_WIDTH / SQUARE_WIDTH][TABLE_HEIGHT / SQUARE_HEIGHT];

        Color currColor = color2;
        for(int i = 0; i < squadrette.length; i++) {

            currColor = currColor == color2 ? color1 : color2;

            for(int j = 0; j < squadrette[i].length; j++) {
                currColor = currColor == color2 ? color1 : color2;

                Quadratino sq = new Quadratino(
                        SQUARE_WIDTH * i, SQUARE_HEIGHT * j,
                        SQUARE_WIDTH, SQUARE_HEIGHT, currColor);
                squadrette[i][j] = sq;
                entities.add(sq);
            }
        }

        // Sort entities

        entities.sort(Comparator.comparingInt(Renderable::getPriority));
    }

    public Quadratino getQuadrato(int x, int y) {
        for(Quadratino[] quadrati : squadrette)
            if (x >= quadrati[0].getMiddleX() - SQUARE_WIDTH / 2 &&
                    x <= quadrati[0].getMiddleX() + SQUARE_WIDTH / 2)
                for (Quadratino quadrato : quadrati)
                    if (y >= quadrato.getMiddleY() - SQUARE_HEIGHT / 2 &&
                            y <= quadrato.getMiddleY() + SQUARE_HEIGHT / 2)
                        return quadrato;
        throw new RuntimeException("There is no quadratino !!!");
    }

    public void onTick() {
        entities.forEach(Entity::onTick);
    }

    public void onRender(RenderContext ctx) {
        entities.forEach(e -> e.onRender(ctx));
    }

    public void onPressKey(int keyCode) {
        entities.forEach(e -> e.onKeyPress(keyCode));
    }

    public void restart() {

        final Quadratino[] middleColomn = squadrette[squadrette.length / 2];
        final Quadratino middleSquare = middleColomn[middleColomn.length / 2];
        cobra.reset(middleSquare);

        for (Quadratino[] squadrette1 : squadrette)
            for (Quadratino squadrette11 : squadrette1)
                squadrette11.setMela(false);
        spawnMela();
    }

    public void spawnMela() {
        Quadratino randomSq;
        do {

            final int randomRow = melaRandom.nextInt(squadrette.length);
            final int randomCol = melaRandom.nextInt(squadrette[randomRow].length);
            randomSq = squadrette[randomRow][randomCol];

        } while(randomSq.hasMela());

        randomSq.setMela(true);
        mela = randomSq;
    }

    public Pitone getCobra() {
        return cobra;
    }

    public Quadratino getMela() {
        return mela;
    }
}
