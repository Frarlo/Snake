package me.ferlo.neat.mutation;

import me.ferlo.neat.gene.Genome;

public interface Mutation {
    boolean mutate(Genome.Unsafe toMutate);
}
