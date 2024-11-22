package io.github.sekassel.moea.termination;

import org.moeaframework.core.*;
import org.moeaframework.core.indicator.WFGHypervolume;

import java.util.function.Function;

public class SteadyState implements TerminationCondition {
    private final double minImprovement;
    private final double maxIterations;

    private Function<NondominatedPopulation, Double> fitnessExtractor;

    private double targetValue;
    private int iterations;

    public SteadyState(double minImprovement, double maxIterations, Function<NondominatedPopulation, Double> fitnessExtractor) {
        this.minImprovement = minImprovement;
        this.maxIterations = maxIterations;
        setFitnessExtractor(fitnessExtractor);
    }

    public SteadyState(Function<NondominatedPopulation, Double> fitnessExtractor) {
        this(0.02, 50, fitnessExtractor);
    }

    @Override
    public void initialize(Algorithm algorithm) {
        targetValue = 0;
    }

    @Override
    public boolean shouldTerminate(Algorithm algorithm) {
        final double value = fitnessExtractor.apply(algorithm.getResult());
        if (Double.isNaN(value)) {
            return false;
        }
        if (value > targetValue) {
            targetValue = value * (1 + minImprovement);
            iterations = 0;
            return false;
        }
        return ++iterations >= maxIterations;
    }

    public void setFitnessExtractor(Function<NondominatedPopulation, Double> fitnessExtractor) {
        this.fitnessExtractor = fitnessExtractor;
    }

    public static final class Hypervolume extends SteadyState {
        private double[] referencePoint;

        public Hypervolume(double minImprovement, double maxIterations, double[] referencePoint) {
            super(minImprovement, maxIterations, null);
            setReferencePoint(referencePoint);
        }

        public Hypervolume(double[] referencePoint) {
            super(null);
            setReferencePoint(referencePoint);
        }

        @Override
        public void initialize(Algorithm algorithm) {
            final Indicator indicator = new WFGHypervolume(algorithm.getProblem(), referencePoint);
            setFitnessExtractor(population -> {
                final NondominatedPopulation approximationSet = new NondominatedPopulation();
                for (final Solution solution : population) {
                    if (solution.isFeasible()) {
                        approximationSet.add(solution);
                    }
                }
                if (approximationSet.isEmpty()) {
                    return Double.NaN;
                }
                return indicator.evaluate(approximationSet);
            });
        }

        public void setReferencePoint(double[] referencePoint) {
            this.referencePoint = referencePoint;
        }
    }
}
