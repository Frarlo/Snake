package me.ferlo.neat.gene;

public class InputNode extends Node {

    public InputNode(int id) {
        super(id);
    }

    public InputNode(InputNode other) {
        super(other);
    }

    @Override
    public InputNode copy() {
        return new InputNode(this);
    }

    @Override
    public float doFeedForward(float input) {
        return input;
    }
}
