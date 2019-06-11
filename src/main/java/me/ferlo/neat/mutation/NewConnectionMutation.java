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
        outer: for(int i = 0; i < 100; i++) {
            final Node inGene = getRandomNode(toMutate);
            final Node outGene = getRandomNodeExcludeInput(toMutate);

            if(inGene.getId() == outGene.getId())
                continue;

            for(Link link : toMutate.getLinks())
                if((link.getIn() == inGene.getId() && link.getOut() == outGene.getId()) ||
                        (link.getOut() == inGene.getId() && link.getIn() == outGene.getId()))
                    continue outer; // Connection already exists

            toMutate.addConnection(new Link(
                    inGene.getId(), outGene.getId(),
                    random.nextFloat() * 4f - 2f,
                    true, core.getAndIncrementInnovation()));
            break;
        }
    }

    private Node getRandomNode(Genome.Unsafe toMutate) {
        int randomN =  random.nextInt(toMutate.getInputSize() + toMutate.getHiddenSize() + toMutate.getOutputSize());

        if(randomN < toMutate.getInputSize())
            return toMutate.getInputNodes().get(randomN);
        randomN -= toMutate.getInputSize();

        if(randomN < toMutate.getHiddenSize())
            return toMutate.getHiddenNodes().get(randomN);
        randomN -= toMutate.getHiddenSize();

//        if(randomN < toMutate.getOutputSize())
        return toMutate.getOutputNodes().get(randomN);
    }

    private Node getRandomNodeExcludeInput(Genome.Unsafe toMutate) {
        int randomN =  random.nextInt(toMutate.getHiddenSize() + toMutate.getOutputSize());

        if(randomN < toMutate.getHiddenSize())
            return toMutate.getHiddenNodes().get(randomN);
        randomN -= toMutate.getHiddenSize();

//        if(randomN < toMutate.getOutputSize())
        return toMutate.getOutputNodes().get(randomN);
    }
}
