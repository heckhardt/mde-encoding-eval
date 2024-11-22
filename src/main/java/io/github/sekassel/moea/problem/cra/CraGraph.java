package io.github.sekassel.moea.problem.cra;

import io.github.sekassel.moea.model.cra.Class;
import io.github.sekassel.moea.model.cra.*;
import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.operator.cra.*;
import io.github.sekassel.moea.problem.TimingProblem;
import io.github.sekassel.moea.variable.ClassModelVariable;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CraGraph extends CraProblem {
    private final List<ClassModelMutation> operators;

    public CraGraph(ClassModel model, List<ClassModelMutation> operators) {
        super(model, 1, 2, 1);
        this.operators = operators;
    }

    @Override
    public void evaluate(Solution solution) {
        final ClassModelVariable variable = (ClassModelVariable) solution.getVariable(0);
        final ClassModel model = variable.getModel();

        final long classlessFeatures = model.getFeatures()
                .stream()
                .filter(feature -> feature.getIsEncapsulatedBy() == null)
                .count();
        solution.setConstraint(0, Constraint.equal(classlessFeatures, 0));

        final Map<Class, List<Attribute>> encapsulatedAttributes = new HashMap<>();
        final Map<Class, List<Method>> encapsulatedMethods = new HashMap<>();

        double cohesionRatio = 0;
        double couplingRatio = 0;
        for (final Class c1 : model.getClasses()) {
            final List<Attribute> encapsulatedAttributes1 = encapsulatedAttributes.computeIfAbsent(c1, CraGraph::a);
            final List<Method> encapsulatedMethods1 = encapsulatedMethods.computeIfAbsent(c1, CraGraph::m);
            cohesionRatio += cohesionRatio(encapsulatedAttributes1, encapsulatedMethods1);
            for (final Class c2 : model.getClasses()) {
                if (c1 != c2) {
                    final List<Attribute> encapsulatedAttributes2 = encapsulatedAttributes.computeIfAbsent(c2, CraGraph::a);
                    final List<Method> encapsulatedMethods2 = encapsulatedMethods.computeIfAbsent(c2, CraGraph::m);
                    couplingRatio += couplingRatio(encapsulatedMethods1, encapsulatedAttributes2, encapsulatedMethods2);
                }
            }
        }
        solution.setObjective(0, -cohesionRatio);
        solution.setObjective(1, couplingRatio);
    }

    private static double cohesionRatio(List<Attribute> encapsulatedAttributes, List<Method> encapsulatedMethods) {
        if (encapsulatedMethods.isEmpty()) {
            // The class does not encapsulate any methods, so we immediately return 0
            return 0;
        } else if (encapsulatedMethods.size() == 1) {
            // The class only encapsulates one method, so we skip calculating MMI
            if (encapsulatedAttributes.isEmpty()) {
                // The class does not encapsulate any attributes, so we skip calculating MAI
                return 0;
            }
            // The class encapsulates at least one attribute, so we return MAI
            return (double) mai(encapsulatedMethods, encapsulatedAttributes) / (encapsulatedMethods.size() * encapsulatedAttributes.size());
        }
        // The class encapsulates at least two methods
        if (encapsulatedAttributes.isEmpty()) {
            // The class does not encapsulate any attributes, so we only return MMI
            return (double) mmi(encapsulatedMethods, encapsulatedMethods) / (encapsulatedMethods.size() * (encapsulatedMethods.size() - 1));
        }
        // The class encapsulates at least two methods and one attribute, so we return MAI + MMI
        return (double) mai(encapsulatedMethods, encapsulatedAttributes) / (encapsulatedMethods.size() * encapsulatedAttributes.size())
                + (double) mmi(encapsulatedMethods, encapsulatedMethods) / (encapsulatedMethods.size() * (encapsulatedMethods.size() - 1));
    }

    private static double couplingRatio(List<Method> encapsulatedMethods1, List<Attribute> encapsulatedAttributes2, List<Method> encapsulatedMethods2) {
        if (encapsulatedMethods1.isEmpty()) {
            // The source class does not encapsulate any methods, so we immediately return 0
            return 0;
        }
        // The source class encapsulates at least one method
        if (encapsulatedMethods2.isEmpty()) {
            // The target class encapsulates  does not encapsulate any methods, so we skip calculating MMI
            if (encapsulatedAttributes2.isEmpty()) {
                // The target class does not encapsulate any attributes, so we skip calculating MAI
                return 0;
            }
            // The target class contains at least one attribute, so we return MAI
            return (double) mai(encapsulatedMethods1, encapsulatedAttributes2) / (encapsulatedMethods1.size() * encapsulatedAttributes2.size());
        }
        // The target class encapsulates at least one method
        if (encapsulatedAttributes2.isEmpty()) {
            // The target class encapsulates no attributes, so we return MMI
            return (double) mmi(encapsulatedMethods1, encapsulatedMethods2) / (encapsulatedMethods1.size() * encapsulatedMethods2.size());
        }
        // The target class encapsulates at least two methods and one attribute, so we return MAI + MMI
        return (double) mai(encapsulatedMethods1, encapsulatedAttributes2) / (encapsulatedMethods1.size() * encapsulatedAttributes2.size())
                + (double) mmi(encapsulatedMethods1, encapsulatedMethods2) / (encapsulatedMethods1.size() * encapsulatedMethods2.size());
    }

    private static long mai(List<Method> encapsulatedMethods, List<Attribute> encapsulatedAttributes) {
        return encapsulatedMethods.stream()
                .map(Method::getDataDependency)
                .mapToLong(dataDependencies -> encapsulatedAttributes.stream()
                        .filter(dataDependencies::contains)
                        .count())
                .sum();
    }

    private static long mmi(List<Method> encapsulatedMethods1, List<Method> encapsulatedMethods2) {
        return encapsulatedMethods1.stream()
                .map(Method::getFunctionalDependency)
                .mapToLong(functionalDependencies -> encapsulatedMethods2.stream()
                        .filter(functionalDependencies::contains)
                        .count())
                .sum();
    }

    private static List<Method> m(Class c) {
        final List<Method> methods = new LinkedList<>();
        for (final Feature feature : c.getEncapsulates()) {
            if (feature instanceof Method method) {
                methods.add(method);
            }
        }
        return methods;
    }

    private static List<Attribute> a(Class c) {
        final List<Attribute> attributes = new LinkedList<>();
        for (final Feature feature : c.getEncapsulates()) {
            if (feature instanceof Attribute attribute) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    @Override
    public Solution newSolution() {
        final Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);
        solution.setVariable(0, new ClassModelVariable(EcoreUtil.copy(model), operators));
        return solution;
    }

    public static NSGAII createAlgorithm(int run, ClassModel model) {
        final List<ClassModelMutation> operators = List.of(
                new AddUnassignedFeatureToNewClass(),
                new AddUnassignedFeatureToExClass(),
                new MoveFeatureToExClass(),
                new MoveFeatureToNewClass(),
                new DeleteEmptyClass()
        );
        final NSGAII nsgaii = new NSGAII(new TimingProblem(new CraGraph(model, operators)));
        nsgaii.setVariation(new RandomMutation(operators));
        nsgaii.setInitialPopulationSize(40);
        return nsgaii;
    }
}
