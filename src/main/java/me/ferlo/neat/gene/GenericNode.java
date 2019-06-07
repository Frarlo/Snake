package me.ferlo.neat.gene;

import me.ferlo.neat.Core;

public class GenericNode extends Node {

    private final Core core;

    public GenericNode(Core core, int id) {
        super(id);
        this.core = core;
    }

    public GenericNode(GenericNode other) {
        super(other);
        core = other.core;
    }

    @Override
    public GenericNode copy() {
        return new GenericNode(this);
    }

    @Override
    public float doFeedForward(float input) {
        return core.getConfig().getActivationFunction().activate(input);
    }
}
