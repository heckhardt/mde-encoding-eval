package io.github.sekassel.moea.operator;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.util.TypedProperties;

import java.util.List;
import java.util.stream.Collectors;

public class RandomMutation implements Mutation {
    private final List<? extends Operator> operators;
    private final IntegerDistribution distribution;

    private long copyDuration = 0;
    private long matchDuration = 0;
    private long mutateDuration = 0;

    public RandomMutation(List<? extends Operator> operators, IntegerDistribution distribution) {
        this.operators = operators;
        this.distribution = distribution;
    }

    public RandomMutation(List<? extends Operator> operators) {
        this(operators, null);
    }

    @Override
    public Solution mutate(Solution parent) {
        final long copyStart = System.nanoTime();
        final Solution offspring = parent.copy();
        copyDuration += System.nanoTime() - copyStart;

        final int steps = distribution == null ? 1 : distribution.sample();
        for (int i = 0; i < steps; i++) {
            final long matchStart = System.nanoTime();
            final List<? extends Operator> applicableOperators = operators.stream()
                    .filter(operator -> operator.canMutate(offspring))
                    .toList();
            if (applicableOperators.isEmpty()) {
                matchDuration += System.nanoTime() - matchStart;
                break;
            }
            final Operator operator = PRNG.nextItem(applicableOperators);
            matchDuration += System.nanoTime() - matchStart;

            final long mutateStart = System.nanoTime();
            operator.mutate(offspring);
            mutateDuration += System.nanoTime() - mutateStart;
        }

        return offspring;
    }

    @Override
    public String getName() {
        return operators.stream()
                .map(Operator::getName)
                .collect(Collectors.joining("|"));
    }

    public void clear() {
        copyDuration = 0;
        matchDuration = 0;
        mutateDuration = 0;
    }

    public long getCopyDuration() {
        return copyDuration;
    }

    public long getMatchDuration() {
        return matchDuration;
    }

    public long getMutateDuration() {
        return mutateDuration;
    }

    @Override
    public void applyConfiguration(TypedProperties properties) {
        for (final Operator operator : operators) {
            operator.applyConfiguration(properties);
        }
    }

    @Override
    public TypedProperties getConfiguration() {
        TypedProperties configuration = new TypedProperties();
        for (final Operator operator : operators) {
            configuration.addAll(operator.getConfiguration());
        }
        return configuration;
    }
}
