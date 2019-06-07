package me.ferlo.neat.gene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Node {

    private final int id;

    private final List<Link> incoming;
    protected float value;

    public Node(int id) {
        this.id = id;
        this.incoming = new ArrayList<>();
    }

    public Node(Node other) {
        this.id = other.id;
        this.incoming = new ArrayList<>(other.incoming);
    }

    public int getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public void addIncoming(Link incoming) {
        this.incoming.add(incoming);
    }

    public List<Link> getIncoming() {
        return incoming;
    }

    public abstract Node copy();

    public float feedForward(float input) {
        this.value = doFeedForward(input);
        return value;
    }

    public abstract float doFeedForward(float input);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
