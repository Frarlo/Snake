package me.ferlo.neat;

@FunctionalInterface
public interface ActivationFunction {

    ActivationFunction CUSTOMIZED_SIGMOID = input -> (float) (1f / (1 + Math.exp(-4.9 * input)));

    float activate(float input);
}
