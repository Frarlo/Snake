package me.ferlo.neat;

import java.util.function.Consumer;

public interface FitnessCalculator {
    void calculateFitness(Model model, Consumer<Float> fitnessConsumer);
}
