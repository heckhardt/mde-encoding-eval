package io.github.sekassel.moea.problem.knapsack;

import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.model.knapsack.KnapsackPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.moeaframework.problem.AbstractProblem;

import java.io.IOException;
import java.util.Map;

public abstract class KnapsackProblem extends AbstractProblem {
    protected final KnapsackModel model;

    public KnapsackProblem(KnapsackModel model, int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
        super(numberOfVariables, numberOfObjectives, numberOfConstraints);
        this.model = model;
    }

    public static KnapsackModel loadModel(URI uri) {
        try {
            final XMIResource resource = new XMIResourceImpl(uri);
            resource.load(Map.of(
                    XMIResource.OPTION_MISSING_PACKAGE_HANDLER,
                    (XMLResource.MissingPackageHandler) nsURI -> KnapsackPackage.eINSTANCE
            ));
            return (KnapsackModel) resource.getContents().getFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "Knapsack";
    }
}
