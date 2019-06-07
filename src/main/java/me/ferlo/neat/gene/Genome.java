package me.ferlo.neat.gene;

import me.ferlo.neat.Core;
import me.ferlo.neat.Model;

import java.util.*;

public class Genome implements Model {

    private final Core core;
    private final Unsafe unsafe;

    private final List<Node> nodes;
    private final Map<Integer, Link> links;

    private final int inputSize;
    private final int outputSize;

    private int maxInnovation = 1;
    private int lastNode = 1;

    private float fitness = 0;

    public Genome(Core core) {
        this.core = core;

        nodes = new ArrayList<>();
        links = new HashMap<>();

        inputSize = core.getConfig().getInputSize();
        outputSize = core.getConfig().getOutputSize();

        this.unsafe = new Unsafe();

        final List<Node> inputNodes = new ArrayList<>();
        for(int i = 0; i < inputSize; i++) {
            final Node inNode = unsafe.newInputNode();
            inputNodes.add(inNode);
            nodes.add(inNode);
        }

        for(Node inNode : inputNodes) {
            for(int j = 0; j < outputSize; j++) {
                final Node outNode = unsafe.newNode();

                nodes.add(outNode);
                unsafe.addConnection(new Link(inNode, outNode,
                        core.getRandom().nextFloat() * 4f - 2f,
                        true, core.getAndIncrementInnovation()));
            }
        }
        generateNetwork();
    }

    public Genome(Genome other) {
        this.core = other.core;

        inputSize = core.getConfig().getInputSize();
        outputSize = core.getConfig().getOutputSize();

        nodes = new ArrayList<>(other.nodes.size());
        links = new HashMap<>(other.links.size());

        this.unsafe = new Unsafe();

        for(Node node : other.nodes)
            nodes.add(node.copy());
        for(Link link : other.links.values())
            unsafe.addConnection(link.copy());

        maxInnovation = other.maxInnovation;
        lastNode = other.lastNode;
    }

    public void randomizeWeights() {
        for (Link link : links.values())
            link.setWeight(core.getRandom().nextFloat() * 4 - 2);
    }

    public static Genome crossover(Core core, Genome p1, Genome p2) {

        if(p1.core != core || p2.core != core)
            throw new UnsupportedOperationException("Core cannot be different than the genomes one");

        final Genome strongest, weakest;
        if(p1.getFitness() > p2.getFitness()) {
            strongest = p1;
            weakest = p2;
        } else {
            strongest = p2;
            weakest = p1;
        }

        final Genome child = new Genome(core);

        for(Link link : strongest.links.values()) {
            final Link link2 = weakest.unsafe.getLinkByInnovation(link.getInnovation());

            // If the link is matching, add it randomly from either parents
            // otherwise just add it from the strongest

            if(link2 != null && core.getRandom().nextBoolean()) {
                final Link copy = link2.copy();

                child.unsafe.addConnection(copy);
                child.unsafe.addHiddenNode(copy.getIn());
                child.unsafe.addHiddenNode(copy.getOut());
            } else {
                final Link copy = link.copy();

                child.unsafe.addConnection(copy);
                child.unsafe.addHiddenNode(copy.getIn());
                child.unsafe.addHiddenNode(copy.getOut());
            }
        }

        return child;
    }

    public float distance(Genome other) {

        int excess = 0;
        int disjoint = 0;

        float weightAvg = 0;
        int weights = 0;

        int shortestInnovation;
        Map<Integer, Link> longest, shortest;

        if(maxInnovation > other.maxInnovation) {
            longest = links;
            shortest = other.links;
            shortestInnovation = other.maxInnovation;
        } else {
            longest = other.links;
            shortest = links;
            shortestInnovation = maxInnovation;
        }

        for(int i = 1; i < longest.size(); i++) {
            final Link link1 = longest.get(i);
            final Link link2 = shortest.get(i);

            if(link1 != null && link2 != null) {
                weightAvg += Math.abs(link1.getWeight() - link2.getWeight());
                weights++;
            } else if(link1 != link2) {
                if (i <= shortestInnovation)
                    disjoint++;
                else // if (i > shortestInn)
                    excess++;
            }
        }

        weightAvg /= weights;
        return core.getConfig().getDistanceExcessCoefficient() * excess / core.getConfig().getDistanceNfactor() +
                core.getConfig().getDistanceDisjointCoefficient() * disjoint / core.getConfig().getDistanceNfactor() +
                core.getConfig().getDistanceAvgWeightCoefficient() * weightAvg;
    }

    public void mutate() {
        core.getMutations().forEach(mutation -> mutation.mutate(unsafe));
        generateNetwork();
    }

    private void generateNetwork() {
        for(Node node : nodes)
            node.getIncoming().clear();
        for(Link link : links.values())
            link.getOut().addIncoming(link);
    }

    public void calculateFitness() {
        fitness = core.getConfig().getFitnessCalculator().calculateFitness(this);
    }

    public float getFitness() {
        return fitness;
    }

    @Override
    public float[] evaluate(float[] inputs) {

        if(inputs.length != inputSize)
            throw new UnsupportedOperationException("Input size and given input differ");

        final List<Node> inputNodes = unsafe.getInputNodes();
        for(int i = 0; i < inputSize; i++)
            inputNodes.get(i).feedForward(inputs[i]);

        for(Node node : nodes) {
            float sum = 0;
            for(Link incoming : node.getIncoming()) {
                final Node other = incoming.getOut();
                sum += incoming.getWeight() * other.getValue();
            }

            if(!(node instanceof InputNode))
                node.feedForward(sum);
        }

        final float[] outputs = new float[outputSize];
        final List<Node> outputNodes = unsafe.getInputNodes();
        for(int i = 0; i < outputSize; i++)
            outputs[i] = outputNodes.get(i).getValue();

        return outputs;
    }

    public class Unsafe {

        private final List<Node> unmodifiableNodes;
        private final Collection<Link> unmodifiableLinks;

        private Unsafe() {
            this.unmodifiableNodes = Collections.unmodifiableList(nodes);
            this.unmodifiableLinks = Collections.unmodifiableCollection(links.values());
        }

        public Node newInputNode() {
            return new InputNode(lastNode++);
        }

        public Node newNode() {
            return new GenericNode(core, lastNode++);
        }

        public void addHiddenNode(Node node) {
            if(!nodes.contains(node)) {
                final int hiddenNodes = nodes.size() - 1 - outputSize;
                nodes.add(hiddenNodes, node);
            }
        }

        public Collection<Link> getLinks() {
            return unmodifiableLinks;
        }

        public Link getLinkByInnovation(int innovation) {
            return links.get(innovation);
        }

        public List<Node> getNodes() {
            return unmodifiableNodes;
        }

        public List<Node> getInputNodes() {
            return unmodifiableNodes.subList(0, inputSize);
        }

        public int getInputSize() {
            return inputSize;
        }

        public List<Node> getOutputNodes() {
            return unmodifiableNodes.subList(nodes.size() - outputSize, nodes.size());
        }

        public int getOutputSize() {
            return outputSize;
        }

        public List<Node> getHiddenNodes() {
            return unmodifiableNodes.subList(inputSize, nodes.size() - outputSize);
        }

        public int getHiddenSize() {
            return nodes.size() - inputSize - outputSize;
        }

        public void addConnection(Link link) {
            links.put(link.getInnovation(), link);

            if(link.getInnovation() > maxInnovation)
                maxInnovation = link.getInnovation();
        }
    }

    public Genome copy() {
        return new Genome(this);
    }
}
