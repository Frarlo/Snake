package me.ferlo.neat;

@FunctionalInterface
public interface ActivationFunction {

    ActivationFunction CUSTOMIZED_SIGMOID = input -> (float) (1f / (1f + Math.exp(-4.9f * input)));

    float activate(float input);
}
