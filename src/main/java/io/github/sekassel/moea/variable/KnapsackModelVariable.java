package io.github.sekassel.moea.variable;

import io.github.sekassel.moea.model.knapsack.*;
import org.apache.commons.lang3.NotImplementedException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

public class KnapsackModelVariable implements Variable {
    private final KnapsackModel model;

    public KnapsackModelVariable(KnapsackModel model) {
        this.model = model;
    }

    @Override
    public Variable copy() {
        final KnapsackFactory kFactory = KnapsackFactory.eINSTANCE;
        final KnapsackPackage kPackage = KnapsackPackage.eINSTANCE;
        final KnapsackModel modelCopy = kFactory.createKnapsackModel();

        for (final Knapsack knapsack : model.getKnapsacks()) {
            final Knapsack knapsackCopy = kFactory.createKnapsack();
            knapsackCopy.setCapacity(knapsack.getCapacity());
            modelCopy.getKnapsacks().add(knapsackCopy);
        }
        for (final Item item : model.getItems()) {
            final Item itemCopy = kFactory.createItem();
            itemCopy.eSet(kPackage.getItem_Weights(), item.getWeights());
            itemCopy.eSet(kPackage.getItem_Values(), item.getValues());
            final Knapsack isContainedBy = item.getIsContainedBy();
            if (isContainedBy != null) {
                final int index = model.getKnapsacks().indexOf(isContainedBy);
                itemCopy.setIsContainedBy(modelCopy.getKnapsacks().get(index));
            }
            modelCopy.getItems().add(itemCopy);
        }
        return new KnapsackModelVariable(modelCopy);
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
