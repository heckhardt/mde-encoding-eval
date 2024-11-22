package io.github.sekassel.moea.variable;

import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.operator.cra.ClassModelMutation;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

import java.util.List;

public class ClassModelVariable implements Variable {
    private final ClassModel model;
    private final List<ClassModelMutation> operators;

    public ClassModelVariable(ClassModel model, List<ClassModelMutation> operators) {
        this.model = model;
        this.operators = operators;
    }

    @Override
    public Variable copy() {
        return new ClassModelVariable(EcoreUtil.copy(model), operators);
    }

    @Override
    public void randomize() {
        for (int i = 0; i < 2; i++) {
            final List<ClassModelMutation> applicableOperators = operators.stream()
                    .filter(operator -> operator.canMutate(model))
                    .toList();
            PRNG.nextItem(applicableOperators).mutate(model);
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

    public ClassModel getModel() {
        return model;
    }
}
