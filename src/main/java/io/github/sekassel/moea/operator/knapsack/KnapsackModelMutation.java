package io.github.sekassel.moea.operator.knapsack;

import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.operator.Operator;
import io.github.sekassel.moea.variable.KnapsackModelVariable;
import org.moeaframework.core.Solution;

public abstract class KnapsackModelMutation implements Operator {
    @Override
    public final void mutate(Solution offspring) {
        mutate(((KnapsackModelVariable) offspring.getVariable(0)).getModel());
    }

    public abstract void mutate(KnapsackModel model);

    @Override
    public final boolean canMutate(Solution parent) {
        return canMutate(((KnapsackModelVariable) parent.getVariable(0)).getModel());
    }

    public abstract boolean canMutate(KnapsackModel model);
}
