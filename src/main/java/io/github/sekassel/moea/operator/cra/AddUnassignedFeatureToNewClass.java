package io.github.sekassel.moea.operator.cra;

import io.github.sekassel.moea.model.cra.Class;
import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.model.cra.CraFactory;
import io.github.sekassel.moea.model.cra.Feature;
import org.moeaframework.core.PRNG;

import java.util.List;

public class AddUnassignedFeatureToNewClass extends ClassModelMutation {
    private final CraFactory factory;

    public AddUnassignedFeatureToNewClass(CraFactory factory) {
        this.factory = factory;
    }

    public AddUnassignedFeatureToNewClass() {
        this(CraFactory.eINSTANCE);
    }

    @Override
    public boolean canMutate(ClassModel model) {
        return model.getFeatures().stream().anyMatch(feature -> feature.getIsEncapsulatedBy() == null);
    }

    @Override
    public void mutate(ClassModel model) {
        final List<Feature> unassignedFeatures = model.getFeatures()
                .stream()
                .filter(feature -> feature.getIsEncapsulatedBy() == null)
                .toList();
        final Feature unassignedFeature = PRNG.nextItem(unassignedFeatures);

        final Class targetClass = factory.createClass();
        model.getClasses().add(targetClass);

        unassignedFeature.setIsEncapsulatedBy(targetClass);
    }

    @Override
    public String getName() {
        return "addUnassignedFeatureToNewClass";
    }
}
