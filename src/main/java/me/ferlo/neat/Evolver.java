package me.ferlo.neat;

import me.ferlo.neat.gene.Genome;

import java.util.function.Consumer;

public class Evolver {

    private Evolver() {
    }

    public static Genome train(Config.Builder builder, Consumer<Model> onGeneration) {
        return train(builder.build(), onGeneration);
    }

    public static Genome train(Config config, Consumer<Model> onGeneration) {

        final Core core = new Core(config);
        core.getDebugFrame().setTargetGeneration(config.getTargetGeneration());

        final Population population = new Population(core);

        while(true) {
            population.nextGeneration();
            Genome best = population.getBest();

            if(best.getFitness() >= config.getTargetFitness() ||
                    population.getGeneration() >= config.getTargetGeneration()) {
                core.getDebugFrame().dispose();
                return best;
            }

            if(onGeneration != null)
                onGeneration.accept(best);
        }
    }
}
