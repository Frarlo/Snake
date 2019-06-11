package me.ferlo.neat.gene;

import me.ferlo.neat.Core;

public class OutputNode extends HiddenNode {

    public OutputNode(Core core, int id) {
        super(core, id);
    }

    public OutputNode(OutputNode other) {
        super(other);
    }

    @Override
    public OutputNode copy() {
        return new OutputNode(this);
    }
}
