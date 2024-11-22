package io.github.sekassel.moea.problem;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;
import org.moeaframework.problem.ProblemWrapper;
import org.moeaframework.util.TypedProperties;

public class TimingProblem extends ProblemWrapper implements Configurable {
    private long evaluateDuration = 0;

    public TimingProblem(Problem problem) {
        super(problem);
    }

    @Override
    public void evaluate(Solution solution) {
        final long start = System.nanoTime();
        super.evaluate(solution);
        final long end = System.nanoTime();
        evaluateDuration += (end - start);
    }

    @Override
    public TypedProperties getConfiguration() {
        if (problem instanceof Configurable configurable) {
            return configurable.getConfiguration();
        }
        return new TypedProperties();
    }

    public void clear() {
        evaluateDuration = 0;
    }

    public long getEvaluateDuration() {
        return evaluateDuration;
    }
}
