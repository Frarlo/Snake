package me.ferlo.neat;

import me.ferlo.neat.mutation.ChangeWeightsMutation;
import me.ferlo.neat.mutation.Mutation;
import me.ferlo.neat.mutation.NewConnectionMutation;
import me.ferlo.neat.mutation.NewNodeMutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Core {

    private final Config config;
    private final Random random;
    private final List<Mutation> mutations;

    private int currentInnovation;

    public Core(Config config) {
        this.config = config;

        random = new Random();
        currentInnovation = 1;

        final List<Mutation> mutations = new ArrayList<>();
        mutations.add(new NewConnectionMutation(this));
        mutations.add(new NewNodeMutation(this));
        mutations.add(new ChangeWeightsMutation(this));
        this.mutations = Collections.unmodifiableList(mutations);
    }

    public Config getConfig() {
        return config;
    }

    public Random getRandom() {
        return random;
    }

    public List<Mutation> getMutations() {
        return mutations;
    }

    public int getCurrentInnovation() {
        return currentInnovation;
    }

    public int getAndIncrementInnovation() {
        return currentInnovation++;
    }
}
