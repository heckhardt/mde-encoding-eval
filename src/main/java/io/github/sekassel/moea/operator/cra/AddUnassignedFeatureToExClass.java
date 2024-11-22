package io.github.sekassel.moea.operator.cra;

import io.github.sekassel.moea.model.cra.Class;
import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.model.cra.Feature;
import org.moeaframework.core.PRNG;

import java.util.List;

public class AddUnassignedFeatureToExClass extends ClassModelMutation {
    @Override
    public boolean canMutate(ClassModel model) {
        return !model.getClasses().isEmpty() &&
                model.getFeatures().stream().anyMatch(feature -> feature.getIsEncapsulatedBy() == null);
    }

    @Override
    public void mutate(ClassModel model) {
        final List<Feature> unassignedFeatures = model.getFeatures()
                .stream()
                .filter(feature -> feature.getIsEncapsulatedBy() == null)
                .toList();
        final Feature unassignedFeature = PRNG.nextItem(unassignedFeatures);

        final Class targetClass = PRNG.nextItem(model.getClasses());

        unassignedFeature.setIsEncapsulatedBy(targetClass);
    }

    @Override
    public String getName() {
        return "addUnassignedFeatureToExClass";
    }
}
