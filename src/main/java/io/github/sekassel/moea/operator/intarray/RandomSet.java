package io.github.sekassel.moea.operator.intarray;

import io.github.sekassel.moea.operator.Operator;
import io.github.sekassel.moea.variable.IntegerArray;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.util.validate.Validate;

@Prefix("set")
public class RandomSet implements Operator {
    private double probability;

    public RandomSet(double probability) {
        setProbability(probability);
    }

    @Override
    public void mutate(Solution offspring) {
        final IntegerArray integerArray = (IntegerArray) offspring.getVariable(0);
        final int[] array = integerArray.getArray();
        final int min = integerArray.getMin();
        final int max = integerArray.getMax();
        for (int j = 0; j < array.length; j++) {
            if (PRNG.nextDouble() <= probability) {
                final int value = PRNG.nextInt(min, max - 1);
                array[j] = value == array[j] ? max : value;
            }
        }
    }

    @Override
    public String getName() {
        return "set";
    }

    public double getProbability() {
        return probability;
    }

    @Property("rate")
    public void setProbability(double probability) {
        Validate.that("probability", probability).isProbability();
        this.probability = probability;
    }
}
