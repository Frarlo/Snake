package me.ferlo.neat;

import me.ferlo.neat.gene.Genome;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private final Core core;

    private final List<Species> species = new ArrayList<>();

    private final int populationSize;

    private int generation;
    private Genome best;

    public Population(Core core) {
        this.core = core;

        populationSize = core.getConfig().getPopulation();
        generation = 0;

        final Genome base = new Genome(core);
        for (int i = 0; i < populationSize; i++) {
            final Genome genome = base.copy();
            genome.randomizeWeights();
            specify(genome);
        }
    }

    private void specify(Genome genome) {
        for (Species species : species) {
            if (species.isCompatible(genome)) {
                species.addMember(genome);
                return;
            }
        }

        Species newSpecies = new Species(core, genome);
        species.add(newSpecies);
    }

    public void nextGeneration() {

        final float[] totalAvgFitness = {0};
        for(Species species : species) {
            species.nextGeneration();
            totalAvgFitness[0] += species.getAvgFitness();
        }

        species.removeIf(species -> {
            // Stale species
           if(species.getStaleness() > core.getConfig().getMaxStaleSpecies())
               return true;
            // Weak species
            final int breeds = (int) Math.floor(species.getAvgFitness() / totalAvgFitness[0] * populationSize);
            return breeds < 1;
        });

        best = null;

        final List<Genome> children = new ArrayList<>();
        for (Species species : species) {
            final int breeds = (int) Math.floor(species.getAvgFitness() / totalAvgFitness[0] * populationSize);
            for(int i = 0; i < breeds; i++)
                children.add(species.breedChild());

            species.killAllButTheBest();

            final Genome currBest = species.getBest();
            if(best == null || currBest.getFitness() > best.getFitness())
                best = currBest;
        }

        while(species.size() + children.size() < populationSize) {
            Species species = this.species.get(core.getRandom().nextInt(this.species.size()));
            children.add(species.breedChild());
        }

        for(Genome child : children)
            specify(child);

        generation++;
    }

    public int getGeneration() {
        return generation;
    }

    public Genome getBest() {
        return best;
    }
}
