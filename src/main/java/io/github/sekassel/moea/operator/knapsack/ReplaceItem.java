package io.github.sekassel.moea.operator.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.moeaframework.core.PRNG;

import java.util.List;

public class ReplaceItem extends KnapsackModelMutation {
    @Override
    public void mutate(KnapsackModel model) {
        final List<Knapsack> nonEmptyKnapsacks = model.getKnapsacks()
                .stream()
                .filter(knapsack -> !knapsack.getContains().isEmpty())
                .toList();
        final Knapsack knapsack = PRNG.nextItem(nonEmptyKnapsacks);

        final Item oldItem = PRNG.nextItem(knapsack.getContains());

        final List<Item> unusedItems = model.getItems()
                .stream()
                .filter(item -> item.getIsContainedBy() == null)
                .toList();
        final Item newItem = PRNG.nextItem(unusedItems);

        knapsack.getContains().remove(oldItem);
        knapsack.getContains().add(newItem);
    }

    @Override
    public boolean canMutate(KnapsackModel model) {
        final int numUsedItems = model.getKnapsacks()
                .stream()
                .mapToInt(knapsack -> knapsack.getContains().size())
                .sum();
        return numUsedItems >= 1 && numUsedItems < model.getItems().size();
    }

    @Override
    public String getName() {
        return "replace";
    }
}
