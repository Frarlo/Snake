package me.ferlo.neat.mutation;

import me.ferlo.neat.Config;
import me.ferlo.neat.Core;
import me.ferlo.neat.gene.Genome;

import java.util.Random;

public abstract class BaseMutation implements Mutation {

    protected final Core core;
    protected final Config config;
    protected final Random random;

    protected final float mutationChance;

    public BaseMutation(Core core) {
        this(core, 1);
    }

    public BaseMutation(Core core, float mutationChance) {
        this.core = core;
        this.config = core.getConfig();
        this.random = core.getRandom();
        this.mutationChance = mutationChance;
    }

    @Override
    public boolean mutate(Genome.Unsafe toMutate) {
        if(random.nextFloat() > mutationChance)
            return false;
        doMutate(toMutate);
        return true;
    }

    protected abstract void doMutate(Genome.Unsafe toMutate);
}
