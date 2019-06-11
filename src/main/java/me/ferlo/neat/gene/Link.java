package me.ferlo.neat.gene;

public class Link {

    private final int in;
    private final int out;

    private float weight;
    private boolean enabled;
    private final int innovation;

    public Link(int in, int out, float weight, boolean enabled, int innovation) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.enabled = enabled;
        this.innovation = innovation;
    }

    public Link(Link other) {
        in = other.in;
        out = other.out;
        weight = other.weight;
        enabled = other.enabled;
        innovation = other.innovation;
    }

    public int getIn() {
        return in;
    }

    public int getOut() {
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
