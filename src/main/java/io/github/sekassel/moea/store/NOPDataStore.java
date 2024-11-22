package io.github.sekassel.moea.store;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedPopulation;

public class NOPDataStore implements DataStore {
    @Override
    public int initializeRun(AbstractEvolutionaryAlgorithm algorithm, String instance, String representation) {
        return -1;
    }

    @Override
    public void saveResult(int runId, int iteration, NondominatedPopulation result) {
    }

    @Override
    public void saveStats(int runId, int iteration, long step, long evaluate, long copy, long match, long mutate, long shouldTerminate) {
    }

    @Override
    public void finalizeRun(int runId, int totalIterations) {
    }
}
