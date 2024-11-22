package io.github.sekassel.moea.problem.knapsack;

import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.operator.Operator;
import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.operator.intarray.RandomSet;
import io.github.sekassel.moea.variable.IntegerArray;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.TimingProblem;

import java.util.List;

public class KnapsackIntArray extends KnapsackProblem {
    final int[] knapsackCapacities;
    final int[][] itemWeights;
    final int[][] itemValues;

    public KnapsackIntArray(KnapsackModel model) {
        super(model, 1, model.getKnapsacks().size(), model.getKnapsacks().size());
        knapsackCapacities = model.getKnapsacks()
                .stream()
                .mapToInt(Knapsack::getCapacity)
                .toArray();
        itemWeights = model.getItems()
                .stream()
                .map(item -> item.getWeights()
                        .stream()
                        .mapToInt(Integer::intValue)
                        .toArray())
                .toArray(int[][]::new);
        itemValues = model.getItems()
                .stream()
                .map(item -> item.getValues()
                        .stream()
                        .mapToInt(Integer::intValue)
                        .toArray())
                .toArray(int[][]::new);
    }

    @Override
    public void evaluate(Solution solution) {
        final int[] items = ((IntegerArray) solution.getVariable(0)).getArray();
        final int[] knapsackWeights = new int[numberOfObjectives];
        final int[] knapsackValues = new int[numberOfObjectives];
        for (int i = 0; i < items.length; i++) {
            final int item = items[i];
            if (item > 0) {
                final int knapsackIndex = item - 1;
                knapsackWeights[knapsackIndex] += itemWeights[i][knapsackIndex];
                knapsackValues[knapsackIndex] += itemValues[i][knapsackIndex];
            }
        }
        for (int i = 0; i < numberOfObjectives; i++) {
            solution.setObjective(i, -knapsackValues[i]);
            solution.setConstraint(i, Constraint.lessThanOrEqual(knapsackWeights[i], knapsackCapacities[i]));
        }
    }

    @Override
    public Solution newSolution() {
        final Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
        solution.setVariable(0, new IntegerArray(model.getItems().size(), 0, model.getKnapsacks().size()));
        return solution;
    }

    public static NSGAII createAlgorithm(int run, KnapsackModel model) {
        final NSGAII nsgaii = new NSGAII(new TimingProblem(new KnapsackIntArray(model)));
        final List<Operator> operators = List.of(new RandomSet(1.0 / model.getItems().size()));
        final RandomMutation variation = new RandomMutation(operators);
        nsgaii.setVariation(variation);
        nsgaii.setInitialPopulationSize((model.getItems().size() / 250 + model.getKnapsacks().size()) * 50);
        return nsgaii;
    }
}
