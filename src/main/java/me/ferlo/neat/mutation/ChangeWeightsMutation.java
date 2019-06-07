package me.ferlo.neat.mutation;

import me.ferlo.neat.Core;
import me.ferlo.neat.gene.Genome;
import me.ferlo.neat.gene.Link;

public class ChangeWeightsMutation extends BaseMutation {

    public ChangeWeightsMutation(Core core) {
        super(core, core.getConfig().getConnectionWeightsMutationChance());
    }

    @Override
    public void doMutate(Genome.Unsafe toMutate) {
        for(Link link : toMutate.getLinks()) {
            if(random.nextFloat() < core.getConfig().getConnectionWeightsPerturbationChance())
                link.setWeight(link.getWeight() + random.nextFloat() * 0.1f * 2 - 0.1f);
            else
                link.setWeight(random.nextFloat() * 4 - 2);
        }
    }
}
