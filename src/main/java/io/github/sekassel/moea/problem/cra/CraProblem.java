package io.github.sekassel.moea.problem.cra;

import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.model.cra.CraPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.moeaframework.problem.AbstractProblem;

import java.io.IOException;
import java.util.Map;

public abstract class CraProblem extends AbstractProblem {
    protected final ClassModel model;

    public CraProblem(ClassModel model, int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
        super(numberOfVariables, numberOfObjectives, numberOfConstraints);
        this.model = model;
    }

    public static ClassModel loadModel(URI uri) {
        try {
            final XMIResource resource = new XMIResourceImpl(uri);
            resource.load(Map.of(
                    XMIResource.OPTION_MISSING_PACKAGE_HANDLER,
                    (XMLResource.MissingPackageHandler) nsURI -> CraPackage.eINSTANCE
            ));
            return (ClassModel) resource.getContents().getFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "CRA";
    }
}
