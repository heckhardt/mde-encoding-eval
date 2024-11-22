package io.github.sekassel.moea.problem.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.operator.knapsack.*;
import io.github.sekassel.moea.variable.KnapsackModelVariable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.TimingProblem;

import java.util.List;

public class KnapsackGraph extends KnapsackProblem {
    public KnapsackGraph(KnapsackModel model) {
        super(model, 1, model.getKnapsacks().size(), model.getKnapsacks().size());
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
        solution.setVariable(0, new KnapsackModelVariable(EcoreUtil.copy(model)));
        return solution;
    }

    public static NSGAII createAlgorithm(int run, KnapsackModel model) {
        final NSGAII nsgaii = new NSGAII(new TimingProblem(new KnapsackGraph(model)));
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
}
