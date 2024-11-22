package io.github.sekassel.moea.operator.cra;

import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.operator.Operator;
import io.github.sekassel.moea.variable.ClassModelVariable;
import org.moeaframework.core.Solution;

public abstract class ClassModelMutation implements Operator {
    @Override
    public final boolean canMutate(Solution parent) {
        return canMutate(((ClassModelVariable) parent.getVariable(0)).getModel());
    }

    public abstract boolean canMutate(ClassModel model);

    @Override
    public final void mutate(Solution offspring) {
        mutate(((ClassModelVariable) offspring.getVariable(0)).getModel());
    }

    public abstract void mutate(ClassModel model);
}
