package me.ferlo.neat.gene;

public class Link {

    private final Node in;
    private final Node out;

    private float weight;
    private boolean enabled;
    private final int innovation;

    public Link(Node in, Node out, float weight, boolean enabled, int innovation) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.enabled = enabled;
        this.innovation = innovation;
    }

    public Link(Link other) {
        in = other.in.copy();
        out = other.out.copy();
        weight = other.weight;
        enabled = other.enabled;
        innovation = other.innovation;
    }

    public Node getIn() {
        return in;
    }

    public Node getOut() {
        return out;
    }

    public float getWeight() {
        return weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getInnovation() {
        return innovation;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Link copy() {
        return new Link(this);
    }
}
