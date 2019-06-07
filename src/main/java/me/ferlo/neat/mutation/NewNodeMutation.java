package me.ferlo.neat.mutation;

import me.ferlo.neat.Core;
import me.ferlo.neat.gene.Genome;
import me.ferlo.neat.gene.Link;
import me.ferlo.neat.gene.Node;

public class NewNodeMutation extends BaseMutation {

    public NewNodeMutation(Core core) {
        super(core, core.getConfig().getNewNodeMutationChance());
    }

    @Override
    public void doMutate(Genome.Unsafe toMutate) {

        Link oldConn;
        do {
            final int randomN = random.nextInt(toMutate.getLinks().size());
            oldConn = toMutate.getLinkByInnovation(randomN);
        } while (oldConn == null);

        final Node newNode = toMutate.newNode();
        toMutate.addHiddenNode(newNode);

        toMutate.addConnection(new Link(oldConn.getIn(), newNode, 1,
                true, core.getAndIncrementInnovation()));
        toMutate.addConnection(new Link(newNode, oldConn.getOut(), oldConn.getWeight(),
                true, core.getAndIncrementInnovation()));

        oldConn.setEnabled(false);
    }
}
