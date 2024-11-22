package io.github.sekassel.moea.operator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Configurable;

public interface Operator extends Configurable {
    default boolean canMutate(Solution parent) {
        return true;
    }

    void mutate(Solution offspring);

    String getName();
}
