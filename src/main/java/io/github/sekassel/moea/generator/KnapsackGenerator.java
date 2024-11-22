package io.github.sekassel.moea.generator;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackFactory;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

public class KnapsackGenerator {
    private static final int LOWER_BOUND = 10;
    private static final int UPPER_BOUND = 100;

    private static final int[] NS = new int[]{2, 3, 4};
    private static final int[] MS = new int[]{250, 500, 750};

    private static final RandomGenerator RANDOM_GENERATOR = RandomGenerator.of("L32X64MixRandom");
    private static final KnapsackFactory FACTORY = KnapsackFactory.eINSTANCE;

    private static int nextValue() {
        return RANDOM_GENERATOR.nextInt(LOWER_BOUND, UPPER_BOUND + 1);
    }

    private static int nextWeight() {
        return RANDOM_GENERATOR.nextInt(LOWER_BOUND, UPPER_BOUND + 1);
    }

    public static KnapsackModel generate(int n, int m) {
        final KnapsackModel model = FACTORY.createKnapsackModel();
        for (int j = 0; j < m; j++) {
            final Item item = FACTORY.createItem();
            for (int i = 0; i < n; i++) {
                item.getValues().add(nextValue());
            }
            for (int i = 0; i < n; i++) {
                item.getWeights().add(nextWeight());
            }
            model.getItems().add(item);
        }
        for (int i = 0; i < n; i++) {
            final Knapsack knapsack = FACTORY.createKnapsack();
            final int index = i;
            final int capacity = model.getItems()
                    .stream()
                    .mapToInt(item -> item.getWeights().get(index))
                    .sum() / 2;
            knapsack.setCapacity(capacity);
            model.getKnapsacks().add(knapsack);
        }
        return model;
    }

    public static void main(String[] args) throws IOException {
        final Path dir = Path.of("input/knapsack");
        Files.createDirectories(dir);
        char identifier = 'A';
        for (final int n : NS) {
            for (final int m : MS) {
                final Path file = dir.resolve("Input_" + identifier++ + ".xmi");
                try (final BufferedWriter writer = Files.newBufferedWriter(file)) {
                    final XMIResource resource = new XMIResourceImpl();
                    resource.getContents().add(generate(n, m));
                    resource.save(writer, null);
                }
            }
        }
    }
}
