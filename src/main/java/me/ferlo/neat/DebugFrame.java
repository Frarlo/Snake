/*
 * Created by JFormDesigner on Sat Jun 08 19:06:43 CEST 2019
 */

package me.ferlo.neat;

import me.ferlo.snake.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DebugFrame extends JFrame {
    public DebugFrame() {
        initComponents();
    }

    private void speedButtonActionPerformed(ActionEvent e) {
        Snake.getInstance().skipSleep = speedButton.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        JLabel genLabel = new JLabel();
        genValue = new JLabel();
        JLabel speciesLabel = new JLabel();
        speciesValue = new JLabel();
        JLabel genomeLabel = new JLabel();
        genomeValue = new JLabel();
        JLabel fitnessLabel = new JLabel();
        fitnessValue = new JLabel();
        JLabel maxFitnessLabel = new JLabel();
        maxValue = new JLabel();
        speedButton = new JToggleButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(6, 2));

        //---- genLabel ----
        genLabel.setText("Generation");
        contentPane.add(genLabel);
        contentPane.add(genValue);

        //---- speciesLabel ----
        speciesLabel.setText("Species");
        contentPane.add(speciesLabel);
        contentPane.add(speciesValue);

        //---- genomeLabel ----
        genomeLabel.setText("Genome");
        contentPane.add(genomeLabel);
        contentPane.add(genomeValue);

        //---- fitnessLabel ----
        fitnessLabel.setText("Fitness");
        contentPane.add(fitnessLabel);
        contentPane.add(fitnessValue);

        //---- maxFitnessLabel ----
        maxFitnessLabel.setText("Max Fitness");
        contentPane.add(maxFitnessLabel);
        contentPane.add(maxValue);

        //---- speedButton ----
        speedButton.setText("Toggle speed");
        speedButton.addActionListener(e -> speedButtonActionPerformed(e));
        contentPane.add(speedButton);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel genValue;
    private JLabel speciesValue;
    private JLabel genomeValue;
    private JLabel fitnessValue;
    private JLabel maxValue;
    private JToggleButton speedButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private int generation;
    private int targetGeneration;

    private int species;
    private int currentSpecies;

    private int genomes;
    private int currentGenome;

    private float maxFitness;

    public void setCurrentGeneration(int generation) {
        this.generation = generation;
        this.genValue.setText(generation + "/" + targetGeneration);
    }

    public void setTargetGeneration(int targetGeneration) {
        this.targetGeneration = targetGeneration;
        this.genValue.setText(generation + "/" + targetGeneration);
    }

    public void setSpecies(int species) {
        this.species = species;
        this.speciesValue.setText(currentSpecies + "/" + species);
    }

    public void setCurrentSpecies(int currentSpecies) {
        this.currentSpecies = currentSpecies;
        this.speciesValue.setText(currentSpecies + "/" + species);
    }

    public void setGenomes(int genomes) {
        this.genomes = genomes;
        this.genomeValue.setText(currentGenome + "/" + genomes);
    }

    public void setCurrentGenome(int currentGenome) {
        this.currentGenome = currentGenome;
        this.genomeValue.setText(currentGenome + "/" + genomes);
    }

    public void setFitness(float fitness) {
        fitnessValue.setText(String.valueOf(fitness));
        if(fitness > maxFitness)
            setMaxFitness(fitness);
    }

    public void setMaxFitness(float maxFitness) {
        this.maxFitness = maxFitness;
        maxValue.setText(String.valueOf(maxFitness));
    }
}
