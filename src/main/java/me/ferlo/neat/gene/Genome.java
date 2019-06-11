package me.ferlo.neat.gene;

import me.ferlo.neat.Core;
import me.ferlo.neat.Model;

import java.util.*;
import java.util.function.DoubleConsumer;

public class Genome implements Model {

    private final Core core;
    private final Unsafe unsafe;

    private final Map<Integer, Node> idToNode;

    private final List<Node> inputNodes;
    private final List<Node> hiddenNodes;
    private final List<Node> outputNodes;

    private final Map<Integer, Link> links;

    private final int inputSize;
    private final int outputSize;

    private int maxInnovation = 1;
    private int lastNode = 1;

    private float fitness = 0;

    public Genome(Core core) {
        this.core = core;

        inputSize = core.getConfig().getInputSize();
        outputSize = core.getConfig().getOutputSize();

        idToNode = new HashMap<>();

        inputNodes = new ArrayList<>(inputSize);
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>(outputSize);

        links = new HashMap<>();

        this.unsafe = new Unsafe();

        for(int i = 0; i < inputSize; i++)
            unsafe.addNode(unsafe.newInputNode());

        for(int j = 0; j < outputSize; j++) {
            final Node outNode = unsafe.newOutputNode();
            unsafe.addNode(outNode);

            for(Node inNode : inputNodes) {
                unsafe.addConnection(new Link(
                        inNode.getId(), outNode.getId(),
                        core.getRandom().nextFloat() * 4f - 2f,
                        true, core.getAndIncrementInnovation()));
            }
        }
    }

    public Genome(Genome other) {
        this.core = other.core;

        inputSize = core.getConfig().getInputSize();
        outputSize = core.getConfig().getOutputSize();

        idToNode = new HashMap<>();

        inputNodes = new ArrayList<>(other.inputNodes.size());
        hiddenNodes = new ArrayList<>(other.hiddenNodes.size());
        outputNodes = new ArrayList<>(other.outputNodes.size());

        links = new HashMap<>(other.links.size());

        this.unsafe = new Unsafe();

        new ArrayList<>(Arrays.asList(other.inputNodes, other.hiddenNodes, other.outputNodes))
                .forEach(old -> {
                    for(Node node : old)
                        unsafe.addNode(node.copy());
                });

        for(Link link : other.links.values())
            unsafe.addConnection(link.copy());

        maxInnovation = other.maxInnovation;
        lastNode = other.lastNode;
    }

    public void randomizeWeights() {
        for(Link link : links.values())
            link.setWeight(core.getRandom().nextFloat() * 4f - 2f);
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
                child.unsafe.addNode(weakest.idToNode.get(copy.getIn()));
                child.unsafe.addNode(weakest.idToNode.get(copy.getOut()));
            } else {
                final Link copy = link.copy();

                child.unsafe.addConnection(copy);
                child.unsafe.addNode(strongest.idToNode.get(copy.getIn()));
                child.unsafe.addNode(strongest.idToNode.get(copy.getOut()));
            }
        }

        return child;
    }

    public float distance(Genome other) {

        int excess = 0;
        int disjoint = 0;

        float weightAvg = 0;
        int weights = 0;

        int shortestInnovation, longestInnovation;
        Map<Integer, Link> longest, shortest;

        if(maxInnovation > other.maxInnovation) {
            longest = links;
            longestInnovation = maxInnovation;
            shortest = other.links;
            shortestInnovation = other.maxInnovation;
        } else {
            longest = other.links;
            longestInnovation = other.maxInnovation;
            shortest = links;
            shortestInnovation = maxInnovation;
        }

        for(int i = 1; i <= longestInnovation; i++) {
            final Link link1 = longest.get(i);
            final Link link2 = shortest.get(i);

            if(link1 != null && link2 != null) {
                weightAvg += Math.abs(link1.getWeight() - link2.getWeight());
                weights++;
            } else if(link1 != link2) {
                if (i <= shortestInnovation)
                    disjoint++;
                else // if (i > shortestInnovation)
                    excess++;
            }
        }

        if(weights != 0)
            weightAvg /= weights;
        return core.getConfig().getDistanceExcessCoefficient() * excess / core.getConfig().getDistanceNfactor() +
                core.getConfig().getDistanceDisjointCoefficient() * disjoint / core.getConfig().getDistanceNfactor() +
                core.getConfig().getDistanceAvgWeightCoefficient() * weightAvg;
    }

    public void mutate() {
        core.getMutations().forEach(mutation -> mutation.mutate(unsafe));
    }

    private void generateNetwork() {
        new ArrayList<>(Arrays.asList(inputNodes, hiddenNodes, outputNodes))
                .forEach(nodes -> {
                    for(Node node : inputNodes)
                        node.getIncoming().clear();
                });
        for(Link link : links.values())
            if(link.isEnabled())
                idToNode.get(link.getOut()).addIncoming(link);
    }

    public void calculateFitness() {
        generateNetwork();
        core.getConfig().getFitnessCalculator().calculateFitness(this, fitness -> {
            this.fitness = fitness;
            core.getDebugFrame().setFitness(fitness);
        });
    }

    public float getFitness() {
        return fitness;
    }

    @Override
    public float[] evaluate(float[] inputs) {

        if(inputs.length != inputSize)
            throw new UnsupportedOperationException("Input size and given input differ");

        final Set<Node> alreadyCalculated = new HashSet<>();

        final List<Node> inputNodes = unsafe.getInputNodes();
        final List<Node> outputNodes = unsafe.getOutputNodes();

        final float[] outputs = new float[outputSize];

        for(int i = 0; i < inputSize; i++) {
            inputNodes.get(i).feedForward(inputs[i]);
            alreadyCalculated.add(inputNodes.get(i));
        }

        for(int i = 0; i < outputSize; i++) {

//            final float eval0 = evaluateNode0(outputNodes.get(i), alreadyCalculated);
            final float eval1 = evaluateNode(outputNodes.get(i), alreadyCalculated);

//            if(Math.abs(eval0 - eval1) > 0.01)
//                throw new UnsupportedOperationException("uwot " + eval0 + " " + eval1);

            outputs[i] = eval1;
            alreadyCalculated.add(outputNodes.get(i));
        }

//        for(Node node : nodes) {
//            float sum = 0;
//            for(Link incoming : node.getIncoming()) {
//                final Node other = incoming.getIn();
//                sum += incoming.getWeight() * other.getValue();
//            }
//
//            if(!(node instanceof InputNode))
//                node.feedForward(sum);
//        }
//
//        final float[] outputs = new float[outputSize];
//        final List<Node> outputNodes = unsafe.getOutputNodes();
//        for(int i = 0; i < outputSize; i++)
//            outputs[i] = outputNodes.get(i).getValue();

        return outputs;
    }

    public float evaluateNode0(Node node, Set<Node> alreadyCalculated) {
        if(alreadyCalculated.contains(node))
            return node.getValue();

        float sum = 0;
        for(Link incoming : node.getIncoming()) {
            final Node other = idToNode.get(incoming.getIn());
            sum += incoming.getWeight() * evaluateNode0(other, alreadyCalculated);
        }

        node.feedForward(sum);
        alreadyCalculated.add(node);
        return node.getValue();
    }

    public float evaluateNode(Node startNode, Set<Node> alreadyCalculated) {

        final float[] res = new float[] { 0 };

        final Deque<Map.Entry<Node, DoubleConsumer>> stack = new LinkedList<>();
        stack.push(new AbstractMap.SimpleEntry<>(startNode, value -> res[0] = (float) value));

        while(!stack.isEmpty()) {

            final Map.Entry<Node, DoubleConsumer> entry = stack.pop();
            final Node node = entry.getKey();
            final DoubleConsumer consumer = entry.getValue();

            if(alreadyCalculated.contains(node)) {
                consumer.accept(node.getValue());
                continue;
            }

            final float[] sum = { 0, 0 };
            if(node.getIncoming().isEmpty()) {
                node.feedForward(sum[0]);
                alreadyCalculated.add(node);
                consumer.accept(node.getValue());
                continue;
            }

            for(Link incoming : node.getIncoming()) {
                final Node other = idToNode.get(incoming.getIn());

                alreadyCalculated.add(node); // Prevent loops
                stack.push(new AbstractMap.SimpleEntry<>(other, value -> {
                    sum[0] += incoming.getWeight() * value;
                    sum[1] += 1;

                    if(sum[1] >= node.getIncoming().size()) {
                        node.feedForward(sum[0]);
                        alreadyCalculated.add(node);
                        consumer.accept(node.getValue());
                    }
                }));
            }
        }

        return res[0];
    }

    public class Unsafe {

        private final List<Node> unmodifiableInputNodes;
        private final List<Node> unmodifiableOutputNodes;
        private final List<Node> unmodifiableHiddenNodes;

        private final Collection<Link> unmodifiableLinks;

        private Unsafe() {
            this.unmodifiableInputNodes = Collections.unmodifiableList(inputNodes);
            this.unmodifiableHiddenNodes = Collections.unmodifiableList(hiddenNodes);
            this.unmodifiableOutputNodes = Collections.unmodifiableList(outputNodes);
            this.unmodifiableLinks = Collections.unmodifiableCollection(links.values());
        }

        public Node newInputNode() {
            return new InputNode(lastNode++);
        }

        public Node newOutputNode() {
            return new OutputNode(core, lastNode++);
        }

        public Node newNode() {
            return new HiddenNode(core, lastNode++);
        }

        public void addNode(Node node) {
            if(idToNode.containsKey(node.getId()))
                return;
            idToNode.put(node.getId(), node);

            if(node instanceof InputNode)
                inputNodes.add(node);
            else if(node instanceof OutputNode)
                outputNodes.add(node);
            else if(node instanceof HiddenNode)
                hiddenNodes.add(node);
        }

        public Collection<Link> getLinks() {
            return unmodifiableLinks;
        }

        public Link getLinkByInnovation(int innovation) {
            return links.get(innovation);
        }

        public List<Node> getInputNodes() {
            return unmodifiableInputNodes;
        }

        public int getInputSize() {
            return inputSize;
        }

        public List<Node> getOutputNodes() {
            return unmodifiableOutputNodes;
        }

        public int getOutputSize() {
            return outputSize;
        }

        public List<Node> getHiddenNodes() {
            return unmodifiableHiddenNodes;
        }

        public int getHiddenSize() {
            return unmodifiableHiddenNodes.size();
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
