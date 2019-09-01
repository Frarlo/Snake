package me.ferlo.neat;

import me.ferlo.neat.gene.Genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Species {

    private final Core core;
    private final ExecutorService executor = Executors.newFixedThreadPool(100);

    private Genome representative;
    private final List<Genome> members;

    private float currMaxFitness;
    private float maxFitness;
    private float avgFitness;

    private int staleness;

    public Species(Core core, Genome representative) {
        this.representative = representative;
        this.core = core;

        members = new ArrayList<>();
        members.add(representative);

        staleness = 0;
        maxFitness = 0;
    }

    public void nextGeneration() {

        core.getDebugFrame().setGenomes(members.size());

        final List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            final Genome genome = members.get(i);
            core.getDebugFrame().setCurrentGenome(i + 1);
            futures.add(executor.submit(genome::calculateFitness));
        }

        try {
            for (Future<?> f : futures)
                f.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        final List<Genome> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparingDouble(Genome::getFitness));

        final int toRemove = (int) Math.ceil(sorted.size() * core.getConfig().getGenerationEliminationPercentage());
        for (int i = 0; i < toRemove && sorted.size() > 1; i++)
            sorted.remove(sorted.size() - 1);

        this.members.clear();
        this.members.addAll(sorted);

        currMaxFitness = sorted.get(0).getFitness();
        if (currMaxFitness > maxFitness) {
            maxFitness = currMaxFitness;
            staleness = 0;
        } else {
            staleness++;
        }

        avgFitness = 0;
        for(Genome genome : members)
            avgFitness += genome.getFitness();
        if(!members.isEmpty())
            avgFitness /= members.size();

        representative = sorted.get(0);
    }

    public void killAllButTheBest() {
        final List<Genome> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparingDouble(Genome::getFitness));

        this.members.clear();
        this.members.add(sorted.get(0));

        representative = sorted.get(0);
    }

    public Genome breedChild() {
        final Genome child;

        if(core.getRandom().nextFloat() <= core.getConfig().getCrossoverChance()) {
            final Genome parent1 = members.get(core.getRandom().nextInt(members.size()));
            final Genome parent2 = members.get(core.getRandom().nextInt(members.size()));
            child = Genome.crossover(core, parent1, parent2);
        } else {
            final Genome parent = members.get(core.getRandom().nextInt(members.size()));
            child = parent.copy();
        }

        child.mutate();
        return child;
    }

    public Genome getBest() {
        Genome best = null;
        for(Genome genome : members)
            if(best == null || genome.getFitness() > best.getFitness())
                best = genome;
        return best;
    }

    public boolean isCompatible(Genome genome) {
        return this.representative.distance(genome) <= core.getConfig().getDistanceCompatibilityThreshold();
    }

    public void addMember(Genome genome) {
        members.add(genome);
    }

    public int getStaleness() {
        return staleness;
    }

    public float getMaxFitness() {
        return currMaxFitness;
    }

    public float getAvgFitness() {
        return avgFitness;
    }
}
