package me.ferlo.neat;

public class Config {

    private final int inputSize;
    private final int outputSize;

    private final FitnessCalculator fitnessCalculator;
    private final ActivationFunction activationFunction;

    private final int targetGeneration;
    private final float targetFitness;

    private final int population;
    private final float generationEliminationPercentage;
    private final int maxStaleSpecies;
    private final float crossoverChance;

    private final float distanceExcessCoefficient;
    private final float distanceDisjointCoefficient;
    private final float distanceAvgWeightCoefficient;
    private final float distanceNfactor;

    private final float distanceCompatibilityThreshold;

    private final float connectionWeightsMutationChance;
    private final float connectionWeightsPerturbationChance;

    private final float newNodeMutationChance;
    private final float newConnectionMutationChance;

    private final float disabledGeneChance;

    private Config(Builder builder) {
        this.inputSize = builder.inputSize;
        this.outputSize = builder.outputSize;
        this.fitnessCalculator = builder.fitnessCalculator;
        this.activationFunction = builder.activationFunction;
        this.targetGeneration = builder.targetGeneration;
        this.targetFitness = builder.targetFitness;
        this.population = builder.population;
        this.maxStaleSpecies = builder.maxStaleSpecies;
        this.crossoverChance = builder.crossoverChance;
        this.generationEliminationPercentage = builder.generationEliminationPercentage;
        this.distanceExcessCoefficient = builder.distanceExcessCoefficient;
        this.distanceDisjointCoefficient = builder.distanceDisjointCoefficient;
        this.distanceAvgWeightCoefficient = builder.distanceAvgWeightCoefficient;
        this.distanceNfactor = builder.distanceNFactor;
        this.distanceCompatibilityThreshold = builder.distanceCompatibilityThreshold;
        this.connectionWeightsMutationChance = builder.connectionWeightsMutationChance;
        this.connectionWeightsPerturbationChance = builder.connectionWeightsPerturbationChance;
        this.newNodeMutationChance = builder.newNodeMutationChance;
        this.newConnectionMutationChance = builder.newConnectionMutationChance;
        this.disabledGeneChance = builder.disabledGeneChance;
    }

    public int getInputSize() {
        return inputSize;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public FitnessCalculator getFitnessCalculator() {
        return fitnessCalculator;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public int getTargetGeneration() {
        return targetGeneration;
    }

    public float getTargetFitness() {
        return targetFitness;
    }

    public int getPopulation() {
        return population;
    }

    public float getGenerationEliminationPercentage() {
        return generationEliminationPercentage;
    }

    public int getMaxStaleSpecies() {
        return maxStaleSpecies;
    }

    public float getCrossoverChance() {
        return crossoverChance;
    }

    public float getDistanceExcessCoefficient() {
        return distanceExcessCoefficient;
    }

    public float getDistanceDisjointCoefficient() {
        return distanceDisjointCoefficient;
    }

    public float getDistanceAvgWeightCoefficient() {
        return distanceAvgWeightCoefficient;
    }

    public float getDistanceNfactor() {
        return distanceNfactor;
    }

    public float getDistanceCompatibilityThreshold() {
        return distanceCompatibilityThreshold;
    }

    public float getConnectionWeightsMutationChance() {
        return connectionWeightsMutationChance;
    }

    public float getConnectionWeightsPerturbationChance() {
        return connectionWeightsPerturbationChance;
    }

    public float getNewNodeMutationChance() {
        return newNodeMutationChance;
    }

    public float getNewConnectionMutationChance() {
        return newConnectionMutationChance;
    }

    public float getDisabledGeneChance() {
        return disabledGeneChance;
    }

    public static Builder newBuilder(int inputSize, int outputSize, FitnessCalculator fitnessCalculator) {
        return new Builder(inputSize, outputSize, fitnessCalculator);
    }

    public static class Builder {

        private int inputSize;
        private int outputSize;

        private FitnessCalculator fitnessCalculator;
        private ActivationFunction activationFunction = ActivationFunction.CUSTOMIZED_SIGMOID;

        private int targetGeneration = Integer.MAX_VALUE;
        private float targetFitness = Float.MAX_VALUE;

        private int population = 150;
        private float generationEliminationPercentage = 0.5F;
        private int maxStaleSpecies = 15;
        private float crossoverChance = 0.25f;

        private float distanceExcessCoefficient = 1.0f;
        private float distanceDisjointCoefficient = 1.0f;
        private float distanceAvgWeightCoefficient = 0.4f;
        private float distanceNFactor = 1f;

        private float distanceCompatibilityThreshold = 3f;

        private float connectionWeightsMutationChance = 0.8f;
        private float connectionWeightsPerturbationChance = 0.9f;

        private float newNodeMutationChance = 0.03f;
        private float newConnectionMutationChance = 0.05f;

        private float disabledGeneChance = 0.75f;

        private Builder(int inputSize, int outputSize, FitnessCalculator fitnessCalculator) {
            this.inputSize = inputSize;
            this.outputSize = outputSize;
            this.fitnessCalculator = fitnessCalculator;
        }

        public Config build() {
            return new Config(this);
        }

        public Builder setInputSize(int inputSize) {
            this.inputSize = inputSize;
            return this;
        }

        public Builder setOutputSize(int outputSize) {
            this.outputSize = outputSize;
            return this;
        }

        public Builder setFitnessCalculator(FitnessCalculator fitnessCalculator) {
            this.fitnessCalculator = fitnessCalculator;
            return this;
        }

        public Builder setActivationFunction(ActivationFunction activationFunction) {
            this.activationFunction = activationFunction;
            return this;
        }

        public Builder setTargetGeneration(int targetGeneration) {
            this.targetGeneration = targetGeneration;
            return this;
        }

        public Builder setTargetFitness(float targetFitness) {
            this.targetFitness = targetFitness;
            return this;
        }

        public Builder setPopulation(int population) {
            this.population = population;
            return this;
        }

        public Builder setGenerationEliminationPercentage(float generationEliminationPercentage) {
            this.generationEliminationPercentage = generationEliminationPercentage;
            return this;
        }

        public Builder setCrossoverChance(float crossoverChance) {
            this.crossoverChance = crossoverChance;
            return this;
        }

        public Builder setMaxStaleSpecies(int maxStaleSpecies) {
            this.maxStaleSpecies = maxStaleSpecies;
            return this;
        }

        public Builder setDistanceExcessCoefficient(float distanceExcessCoefficient) {
            this.distanceExcessCoefficient = distanceExcessCoefficient;
            return this;
        }

        public Builder setDistanceDisjointCoefficient(float distanceDisjointCoefficient) {
            this.distanceDisjointCoefficient = distanceDisjointCoefficient;
            return this;
        }

        public Builder setDistanceAvgWeightCoefficient(float distanceAvgWeightCoefficient) {
            this.distanceAvgWeightCoefficient = distanceAvgWeightCoefficient;
            return this;
        }

        public Builder setDistanceNFactor(float distanceNFactor) {
            this.distanceNFactor = distanceNFactor;
            return this;
        }

        public Builder setDistanceCompatibilityThreshold(float distanceCompatibilityThreshold) {
            this.distanceCompatibilityThreshold = distanceCompatibilityThreshold;
            return this;
        }

        public Builder setConnectionWeightsMutationChance(float connectionWeightsMutationChance) {
            this.connectionWeightsMutationChance = connectionWeightsMutationChance;
            return this;
        }

        public Builder setConnectionWeightsPerturbationChance(float connectionWeightsPerturbationChance) {
            this.connectionWeightsPerturbationChance = connectionWeightsPerturbationChance;
            return this;
        }

        public Builder setNewNodeMutationChance(float newNodeMutationChance) {
            this.newNodeMutationChance = newNodeMutationChance;
            return this;
        }

        public Builder setNewConnectionMutationChance(float newConnectionMutationChance) {
            this.newConnectionMutationChance = newConnectionMutationChance;
            return this;
        }

        public Builder setDisabledGeneChance(float disabledGeneChance) {
            this.disabledGeneChance = disabledGeneChance;
            return this;
        }
    }

}
