package io.github.sekassel.moea.operator.cra;

import io.github.sekassel.moea.model.cra.Class;
import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.model.cra.Feature;
import org.moeaframework.core.PRNG;

import java.util.List;

public class MoveFeatureToExClass extends ClassModelMutation {
    @Override
    public boolean canMutate(ClassModel model) {
        return model.getClasses().size() > 1
                && model.getFeatures().stream().anyMatch(feature -> feature.getIsEncapsulatedBy() != null);
    }

    @Override
    public void mutate(ClassModel model) {
        final List<Feature> assignedFeatures = model.getFeatures()
                .stream()
                .filter(feature -> feature.getIsEncapsulatedBy() != null)
                .toList();
        final Feature assignedFeature = PRNG.nextItem(assignedFeatures);

        final List<Class> targetClasses = model.getClasses()
                .stream()
                .filter(cls -> cls != assignedFeature.getIsEncapsulatedBy())
                .toList();
        final Class targetClass = PRNG.nextItem(targetClasses);

        assignedFeature.setIsEncapsulatedBy(targetClass);
    }

    @Override
    public String getName() {
        return "moveFeatureToExClass";
    }
}
