package io.github.sekassel.moea.problem.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.operator.knapsack.*;
import io.github.sekassel.moea.problem.TimingProblem;
import io.github.sekassel.moea.variable.KnapsackModelVariable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.core.configuration.Property;

import java.util.List;

public class KnapsackGraph extends KnapsackProblem implements Configurable {
    private double fillRatio;

    public KnapsackGraph(KnapsackModel model, double fillRatio) {
        super(model, 1, model.getKnapsacks().size(), model.getKnapsacks().size());
        this.fillRatio = fillRatio;
    }

    @Override
    public void evaluate(Solution solution) {
        final double[] objectives = new double[model.getKnapsacks().size()];

        final KnapsackModelVariable variable = (KnapsackModelVariable) solution.getVariable(0);
        final EList<Knapsack> knapsacks = variable.getModel().getKnapsacks();
        for (int i = 0; i < knapsacks.size(); i++) {
            int weight = 0;
            final Knapsack knapsack = knapsacks.get(i);
            for (final Item item : knapsack.getContains()) {
                objectives[i] -= item.getValues().get(i);
                weight += item.getWeights().get(i);
            }
            solution.setConstraint(i, Constraint.lessThanOrEqual(weight, knapsack.getCapacity()));
        }

        solution.setObjectives(objectives);
    }

    @Override
    public Solution newSolution() {
        final Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
        solution.setVariable(0, new KnapsackModelVariable(EcoreUtil.copy(model), fillRatio));
        return solution;
    }

    public static AbstractEvolutionaryAlgorithm createAlgorithm(int run, KnapsackModel model) {
        final int steps = 10;
        final double fillRatio = (run % (steps + 1)) / (double) steps;
        final NSGAII nsgaii = new NSGAII(new TimingProblem(new KnapsackGraph(model, fillRatio)));
        final List<KnapsackModelMutation> operators = List.of(
                new ReplaceItem(),
                new MoveItem(),
                new AddItem(),
                new RemoveItem()
        );
        final RandomMutation variation = new RandomMutation(operators);
        nsgaii.setVariation(variation);
        nsgaii.setInitialPopulationSize((model.getItems().size() / 250 + model.getKnapsacks().size()) * 50);
        return nsgaii;
    }

    public double getFillRatio() {
        return fillRatio;
    }

    @Property("fillRatio")
    public void setFillRatio(double fillRatio) {
        this.fillRatio = fillRatio;
    }
}
