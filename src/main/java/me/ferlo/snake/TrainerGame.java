package me.ferlo.snake;

import me.ferlo.neat.Config;
import me.ferlo.neat.Evolver;
import me.ferlo.snake.render.swing.SwingRenderManager;

public class TrainerGame extends TrainingGame {

    // Constants

    private static TrainerGame INSTANCE;
    
    private TrainerGame() {
        super(null, f -> {});

        skipSleep = false;
        this.rendererFactory = SwingRenderManager::new;
    }
    
    public static TrainerGame getInstance() {
        if(INSTANCE == null)
            INSTANCE = new TrainerGame();
        return INSTANCE;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void gameLoop() {
        this.model = Evolver.train(Config.newBuilder(6, 4,
                (model, fitnessConsumer) -> new TrainingGame(model, fitnessConsumer).start()),
                model -> {
                    this.model = model;
                    super.gameLoop();
                    restart();
                });

        while(true) {
            super.gameLoop();
            restart();
        }
    }
}
