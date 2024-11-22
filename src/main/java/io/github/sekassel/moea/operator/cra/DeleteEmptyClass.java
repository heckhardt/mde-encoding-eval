package io.github.sekassel.moea.operator.cra;

import io.github.sekassel.moea.model.cra.Class;
import io.github.sekassel.moea.model.cra.ClassModel;
import org.moeaframework.core.PRNG;

import java.util.List;

public class DeleteEmptyClass extends ClassModelMutation {
    @Override
    public boolean canMutate(ClassModel model) {
        return model.getClasses().stream().anyMatch(cls -> cls.getEncapsulates().isEmpty());
    }

    @Override
    public void mutate(ClassModel model) {
        final List<Class> emptyClasses = model.getClasses()
                .stream()
                .filter(cls -> cls.getEncapsulates().isEmpty())
                .toList();
        final Class emptyClass = PRNG.nextItem(emptyClasses);

        model.getClasses().remove(emptyClass);
    }

    @Override
    public String getName() {
        return "deleteEmptyClass";
    }
}
