package io.github.sekassel.moea.variable;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

public class KnapsackModelVariable implements Variable {
    private final KnapsackModel model;

    public KnapsackModelVariable(KnapsackModel model) {
        this.model = model;
    }

    @Override
    public Variable copy() {
        return new KnapsackModelVariable(EcoreUtil.copy(model));
    }

    @Override
    public void randomize() {
        for (Knapsack knapsack : model.getKnapsacks()) {
            knapsack.getContains().clear();
        }
        final double n = model.getKnapsacks().size();
        final double probability = n / (n + 1);
        for (final Item item : model.getItems()) {
            if (PRNG.nextDouble() < probability) {
                item.setIsContainedBy(PRNG.nextItem(model.getKnapsacks()));
            }
        }
    }

    @Override
    public String encode() {
        throw new NotImplementedException();
    }

    @Override
    public void decode(String value) {
        throw new NotImplementedException();
    }

    public KnapsackModel getModel() {
        return model;
    }
}
