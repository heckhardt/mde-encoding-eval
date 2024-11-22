package io.github.sekassel.moea.operator.knapsack;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.moeaframework.core.PRNG;

import java.util.List;

public class MoveItem extends KnapsackModelMutation {
    @Override
    public void mutate(KnapsackModel model) {
        final List<Item> usedItems = model.getKnapsacks()
                .stream()
                .flatMap(knapsack -> knapsack.getContains().stream())
                .toList();
        final Item item = PRNG.nextItem(usedItems);

        final List<Knapsack> possibleKnapsacks = model.getKnapsacks()
                .stream()
                .filter(knapsack -> knapsack != item.getIsContainedBy())
                .toList();
        final Knapsack knapsack = PRNG.nextItem(possibleKnapsacks);

        item.setIsContainedBy(knapsack);
    }

    @Override
    public boolean canMutate(KnapsackModel model) {
        return model.getKnapsacks().size() >= 2 && model.getKnapsacks()
                .stream()
                .anyMatch(knapsack -> !knapsack.getContains().isEmpty());
    }

    @Override
    public String getName() {
        return "move";
    }
}
