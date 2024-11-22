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
    private final double probability;

    public KnapsackModelVariable(KnapsackModel model, double probability) {
        this.model = model;
        this.probability = probability;
    }

    public KnapsackModelVariable(KnapsackModel model) {
        this(model, (double) model.getKnapsacks().size() / (model.getKnapsacks().size() + 1));
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
