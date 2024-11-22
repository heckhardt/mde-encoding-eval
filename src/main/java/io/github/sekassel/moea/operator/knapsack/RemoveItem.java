package io.github.sekassel.moea.operator.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.moeaframework.core.PRNG;

import java.util.List;

public class RemoveItem extends KnapsackModelMutation {
    @Override
    public void mutate(KnapsackModel model) {
        final List<Item> usedItems = model.getKnapsacks()
                .stream()
                .flatMap(knapsack -> knapsack.getContains().stream())
                .toList();
        final Item item = PRNG.nextItem(usedItems);

        item.setIsContainedBy(null);
    }

    @Override
    public boolean canMutate(KnapsackModel model) {
        return model.getKnapsacks()
                .stream()
                .anyMatch(knapsack -> !knapsack.getContains().isEmpty());
    }

    @Override
    public String getName() {
        return "remove";
    }
}
