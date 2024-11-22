package io.github.sekassel.moea;

import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.store.DataStore;
import io.github.sekassel.moea.store.NOPDataStore;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.TerminationCondition;
import org.moeaframework.problem.TimingProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Runner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);
    private static final PathMatcher MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/*.xmi");

    private final ExecutorService executorService;
    private final DataStore dataStore;

    public Runner(ExecutorService executorService, DataStore dataStore) {
        this.executorService = executorService;
        this.dataStore = dataStore;
    }

    public Runner(ExecutorService executorService) {
        this(executorService, new NOPDataStore());
    }

    public Runner(DataStore dataStore) {
        this(ForkJoinPool.commonPool(), dataStore);
    }

    public Runner() {
        this(ForkJoinPool.commonPool(), new NOPDataStore());
    }

    public <T extends EObject> void run(Path basePath, Function<URI, T> loader,
                                        Map<String, BiFunction<Integer, T, AbstractEvolutionaryAlgorithm>> configurators,
                                        Function<T, TerminationCondition> terminationConditionProvider,
                                        int runs, int maxIterations) {
        try (final Stream<Path> stream = Files.walk(basePath)) {
            stream.filter(Files::isRegularFile).filter(MATCHER::matches).forEach(path -> {
                final URI uri = URI.createFileURI(path.toString());
                final T model = loader.apply(uri);
                final String instance = path.getFileName().toString();
                configurators.forEach((representation, configurator) -> {
                    for (int i = 0; i < runs; i++) {
                        final AbstractEvolutionaryAlgorithm algorithm = configurator.apply(i, EcoreUtil.copy(model));
                        final int run = i + 1;
                        executorService.execute(() -> run(algorithm, instance, representation, run, runs, maxIterations,
                                terminationConditionProvider.apply(model)));
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void run(AbstractEvolutionaryAlgorithm algorithm, String instance, String representation, int run, int runs,
                     int maxIterations, TerminationCondition terminationCondition) {
        try {
            LOGGER.atInfo()
                    .addKeyValue("problem", algorithm.getProblem().getName())
                    .addKeyValue("instance", instance)
                    .addKeyValue("representation", representation)
                    .log("Starting run {}/{}", run, runs);

            final int runId = dataStore.initializeRun(algorithm, instance, representation);

            // Initialize the algorithm
            algorithm.initialize();
            dataStore.saveResult(runId, 0, algorithm.getResult());

            terminationCondition.initialize(algorithm);

            final TimingProblem timingProblem = (TimingProblem) algorithm.getProblem();
            final RandomMutation randomMutation = (RandomMutation) algorithm.getVariation();

            // Evolve the population
            int i = 0;
            while (i < maxIterations) {
                final long shouldTerminateStart = System.nanoTime();
                final boolean shouldTerminate = terminationCondition.shouldTerminate(algorithm);
                final long shouldTerminateDuration = System.nanoTime() - shouldTerminateStart;
                if (shouldTerminate) {
                    break;
                }
                i++;

                final long stepStart = System.nanoTime();
                algorithm.step();
                final long stepDuration = System.nanoTime() - stepStart;

                dataStore.saveResult(runId, i, algorithm.getResult());
                dataStore.saveStats(runId, i, stepDuration, timingProblem.getNanoseconds(),
                        randomMutation.getCopyDuration(), randomMutation.getMatchDuration(),
                        randomMutation.getMutateDuration(), shouldTerminateDuration);
                timingProblem.clear();
                randomMutation.clear();
            }

            dataStore.finalizeRun(runId, i);
            if (i < maxIterations) {
                LOGGER.atInfo()
                        .addKeyValue("problem", algorithm.getProblem().getName())
                        .addKeyValue("instance", instance)
                        .addKeyValue("representation", representation)
                        .log("Finished run {}/{} ({} iterations)", run, runs, i);
            } else {
                LOGGER.atWarn()
                        .addKeyValue("problem", algorithm.getProblem().getName())
                        .addKeyValue("instance", instance)
                        .addKeyValue("representation", representation)
                        .log("Stopped run {}/{} ({} iterations, limit reached)", run, runs, i);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void run(AbstractEvolutionaryAlgorithm algorithm, String instance, String representation, int maxIterations,
                    TerminationCondition terminationCondition) {
        run(algorithm, instance, representation, 1, 1, maxIterations, terminationCondition);
    }
}
