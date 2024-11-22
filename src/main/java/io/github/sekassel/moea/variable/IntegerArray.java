package io.github.sekassel.moea.variable;

import org.apache.commons.lang3.NotImplementedException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

public class IntegerArray implements Variable {
    private final int[] array;
    private final int length;
    private int min;
    private int max;

    public IntegerArray(int length, int min, int max) {
        this.array = new int[length];
        this.length = length;
        this.min = min;
        this.max = max;
    }

    public IntegerArray(int[] array, int min, int max) {
        this.array = array;
        this.length = array.length;
        this.min = min;
        this.max = max;
    }

    @Override
    public Variable copy() {
        final int[] array = new int[length];
        System.arraycopy(this.array, 0, array, 0, length);
        return new IntegerArray(array, min, max);
    }

    @Override
    public void randomize() {
        for (int i = 0; i < length; i++) {
            array[i] = PRNG.nextInt(min, max);
        }
    }

    @Override
    public String encode() {
        throw new NotImplementedException();
    }

    @Override
    public void decode(String value) {
        throw new NotImplementedException();
    }

    public int[] getArray() {
        return array;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
