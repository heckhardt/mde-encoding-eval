package io.github.sekassel.moea.store;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;

public interface DataStore {
    int initializeRun(AbstractEvolutionaryAlgorithm algorithm, String instance, String representation) throws Exception;

    void saveResult(int runId, int iteration, NondominatedPopulation result) throws Exception;

    void saveStats(int runId, int iteration, long step, long evaluate, long copy, long match, long mutate,
                   long shouldTerminate) throws Exception;

    void finalizeRun(int runId, int totalIterations) throws Exception;
}
