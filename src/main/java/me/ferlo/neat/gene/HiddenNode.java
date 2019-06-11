package me.ferlo.neat.gene;

import me.ferlo.neat.Core;

public class HiddenNode extends Node {

    private final Core core;

    public HiddenNode(Core core, int id) {
        super(id);
        this.core = core;
    }

    public HiddenNode(HiddenNode other) {
        super(other);
        core = other.core;
    }

    @Override
    public HiddenNode copy() {
        return new HiddenNode(this);
    }

    @Override
    public float doFeedForward(float input) {
        if(incoming.isEmpty())
            return 0;
        return core.getConfig().getActivationFunction().activate(input);
    }
}
