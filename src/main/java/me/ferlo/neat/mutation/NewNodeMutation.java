package me.ferlo.neat.mutation;

import me.ferlo.neat.Core;
import me.ferlo.neat.gene.Genome;
import me.ferlo.neat.gene.Link;
import me.ferlo.neat.gene.Node;

import java.util.Collection;

public class NewNodeMutation extends BaseMutation {

    public NewNodeMutation(Core core) {
        super(core, core.getConfig().getNewNodeMutationChance());
    }

    @Override
    public void doMutate(Genome.Unsafe toMutate) {

        if(toMutate.getLinks().isEmpty())
            return;

        Link oldConn;
        do {
            final int randomN = random.nextInt(toMutate.getLinks().size());
            oldConn = getAt(toMutate.getLinks(), randomN);
        } while (oldConn == null);

        final Node newNode = toMutate.newNode();
        toMutate.addNode(newNode);

        toMutate.addConnection(new Link(
                oldConn.getIn(), newNode.getId(),
                1, true, core.getAndIncrementInnovation()));
        toMutate.addConnection(new Link(
                newNode.getId(), oldConn.getOut(),
                oldConn.getWeight(), true, core.getAndIncrementInnovation()));

        oldConn.setEnabled(false);
    }

    private <T> T getAt(Collection<T> collection, int index) {
        int i = 0;
        for(T t : collection)
            if(i++ == index)
                return t;
        return null;
    }
}
