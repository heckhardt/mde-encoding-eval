package io.github.sekassel.moea.operator.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.moeaframework.core.PRNG;

import java.util.List;

public class AddItem extends KnapsackModelMutation {
    @Override
    public void mutate(KnapsackModel model) {
        final List<Item> unusedItems = model.getItems()
                .stream()
                .filter(item -> item.getIsContainedBy() == null)
                .toList();
        final Item item = PRNG.nextItem(unusedItems);

        final Knapsack knapsack = PRNG.nextItem(model.getKnapsacks());
        knapsack.getContains().add(item);
    }

    @Override
    public boolean canMutate(KnapsackModel model) {
        return model.getItems()
                .stream()
                .anyMatch(item -> item.getIsContainedBy() == null);
    }

    @Override
    public String getName() {
        return "add";
    }
}
