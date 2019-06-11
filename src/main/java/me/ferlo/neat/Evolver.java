package me.ferlo.neat;

import me.ferlo.neat.gene.Genome;

public class Evolver {

    private Evolver() {
    }

    public static Genome train(Config.Builder builder) {
        return train(builder.build());
    }

    public static Genome train(Config config) {

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
        }
    }
}
