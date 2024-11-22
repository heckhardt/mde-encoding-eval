package io.github.sekassel.moea.indicator;

import org.moeaframework.core.Indicator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class OuterDiameter implements Indicator {
    final int numberOfObjectives;

    public OuterDiameter(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    @Override
    public double evaluate(NondominatedPopulation approximationSet) {
        double result = Double.MIN_VALUE;
        for (int i = 0; i < numberOfObjectives; i++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (final Solution solution : approximationSet) {
                final double objective = solution.getObjective(i);
                max = Math.max(max, objective);
                min = Math.min(min, objective);
            }
            result = Math.max(result, max - min);
        }
        return result;
    }
}
