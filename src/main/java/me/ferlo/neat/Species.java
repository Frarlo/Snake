package me.ferlo.neat;

import me.ferlo.neat.gene.Genome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Species {

    private final Core core;

    private Genome representative;
    private final List<Genome> members;

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

        members.forEach(Genome::calculateFitness);

        final List<Genome> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparingDouble(Genome::getFitness));

        int toRemove = (int) Math.ceil(sorted.size() * core.getConfig().getGenerationEliminationPercentage());
        while (toRemove-- > 0)
            sorted.remove(sorted.size() - 1);

        this.members.clear();
        this.members.addAll(sorted);

        if (sorted.get(0).getFitness() > maxFitness) {
            maxFitness = sorted.get(0).getFitness();
            staleness = 0;
        } else {
            staleness++;
        }

        avgFitness = 0;
        for(Genome genome : members)
            avgFitness += genome.getFitness();
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
        return maxFitness;
    }

    public float getAvgFitness() {
        return avgFitness;
    }
}
