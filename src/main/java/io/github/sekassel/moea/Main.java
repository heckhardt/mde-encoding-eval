package io.github.sekassel.moea;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.sekassel.moea.model.cra.Method;
import io.github.sekassel.moea.problem.cra.CraGraph;
import io.github.sekassel.moea.problem.cra.CraIntArray;
import io.github.sekassel.moea.problem.cra.CraProblem;
import io.github.sekassel.moea.problem.knapsack.KnapsackGraph;
import io.github.sekassel.moea.problem.knapsack.KnapsackIntArray;
import io.github.sekassel.moea.problem.knapsack.KnapsackProblem;
import io.github.sekassel.moea.store.DataStore;
import io.github.sekassel.moea.store.PGDataStore;
import io.github.sekassel.moea.termination.SteadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final int parallelism = 20;
        final int maximumPoolSize = 30;

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql:postgres");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.addDataSourceProperty("reWriteBatchedInserts", "true");
        config.setMaximumPoolSize(maximumPoolSize);

        try (final HikariDataSource dataSource = new HikariDataSource(config)) {
            run(parallelism, dataSource);
            evaluate(parallelism, dataSource);
        }
    }

    private static void evaluate(int parallelism, DataSource ds) {
        try (final ExecutorService executor = Executors.newWorkStealingPool(parallelism)) {
            final Evaluation evaluation = new Evaluation(ds, executor);
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("CRA", "TTC_InputRDG_A.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("CRA", "TTC_InputRDG_B.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("CRA", "TTC_InputRDG_C.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("CRA", "TTC_InputRDG_D.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("CRA", "TTC_InputRDG_E.xmi", 2));

            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_A.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_B.xmi", 2));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_C.xmi", 2));

            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_D.xmi", 3));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_E.xmi", 3));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_F.xmi", 3));

            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_G.xmi", 4));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_H.xmi", 4));
            executor.execute(() -> evaluation.calculateNormalizedHypervolume("Knapsack", "Input_I.xmi", 4));
        }
    }

    private static void run(int nThreads, DataSource dataSource) {
        LOGGER.info("Starting warmup runs...");
        try (final ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            final Runner runner = new Runner(executorService);

            final int runs = 1;
            final int maxIterations = 50;
            runCra(runner, runs, maxIterations);
            runKnapsack(runner, runs, maxIterations);
        }
        LOGGER.info("Warmup completed.");

        LOGGER.info("Starting evaluated runs...");
        try (final ExecutorService executorService = Executors.newFixedThreadPool(nThreads)) {
            final DataStore dataStore = new PGDataStore(dataSource);
            final Runner runner = new Runner(executorService, dataStore);

            final int runs = 50;
            final int maxIterations = 5000;
            runCra(runner, runs, maxIterations);
            runKnapsack(runner, runs, maxIterations);
        }
        LOGGER.info("Evaluation completed.");
    }

    private static void runKnapsack(Runner runner, int runs, int maxIterations) {
        final Path basePath = Path.of("input", "knapsack");
        runner.run(basePath, KnapsackProblem::loadModel, Map.of(
                "Model", KnapsackGraph::createAlgorithm,
                "IntArray", KnapsackIntArray::createAlgorithm
        ), model -> {
            final double[] referencePoint = new double[model.getKnapsacks().size()];
            Arrays.fill(referencePoint, 0.0);
            return new SteadyState.Hypervolume(referencePoint);
        }, runs, maxIterations);
    }

    private static void runCra(Runner runner, int runs, int maxIterations) {
        final Path basePath = Path.of("input", "cra");
        runner.run(basePath, CraProblem::loadModel, Map.of(
                "Model", CraGraph::createAlgorithm,
                "IntArray", CraIntArray::createAlgorithm
        ), model -> {
            final double maxCoupling = model.getFeatures()
                    .stream()
                    .mapToDouble(feature -> {
                        if (feature instanceof Method method) {
                            return method.getFunctionalDependency().size() + method.getDataDependency().size();
                        }
                        return 0;
                    })
                    .sum();
            return new SteadyState.Hypervolume(new double[]{0, maxCoupling});
        }, runs, maxIterations);
    }
}
