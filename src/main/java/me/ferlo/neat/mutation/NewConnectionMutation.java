package me.ferlo.neat.mutation;

import me.ferlo.neat.Core;
import me.ferlo.neat.gene.Genome;
import me.ferlo.neat.gene.Link;
import me.ferlo.neat.gene.Node;

public class NewConnectionMutation extends BaseMutation {

    public NewConnectionMutation(Core core) {
        super(core, core.getConfig().getNewConnectionMutationChance());
    }

    @Override
    public void doMutate(Genome.Unsafe toMutate) {

        if(toMutate.getHiddenSize() > 0)
            return;

        outer: while(true) {
            final Node inGene = getRandomNode(toMutate);
            final Node outGene = getRandomNodeExcludeInput(toMutate);

            if(inGene.equals(outGene))
                continue;

            for(Link link : toMutate.getLinks())
                if(link.getIn().equals(inGene) && link.getOut().equals(outGene))
                    continue outer; // Connection already exists

            toMutate.addConnection(new Link(inGene, outGene,
                    random.nextFloat() * 4f - 2f,
                    true, core.getAndIncrementInnovation()));
            break;
        }
    }

    private Node getRandomNode(Genome.Unsafe toMutate) {
        return toMutate.getNodes().get(random.nextInt(toMutate.getNodes().size()));
    }

    private Node getRandomNodeExcludeInput(Genome.Unsafe toMutate) {
        return toMutate.getNodes().get(toMutate.getInputSize() +
                random.nextInt(toMutate.getNodes().size() - toMutate.getInputSize()));
    }
}
