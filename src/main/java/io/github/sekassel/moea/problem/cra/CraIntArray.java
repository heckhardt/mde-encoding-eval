package io.github.sekassel.moea.problem.cra;

import io.github.sekassel.moea.model.cra.Attribute;
import io.github.sekassel.moea.model.cra.ClassModel;
import io.github.sekassel.moea.model.cra.Feature;
import io.github.sekassel.moea.model.cra.Method;
import io.github.sekassel.moea.operator.Operator;
import io.github.sekassel.moea.operator.RandomMutation;
import io.github.sekassel.moea.operator.intarray.RandomSet;
import io.github.sekassel.moea.variable.IntegerArray;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.problem.TimingProblem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CraIntArray extends CraProblem {
    private final Set<Integer>[] dataDependencies;
    private final Set<Integer>[] functionalDependencies;
    private final boolean[] isMethod;

    public CraIntArray(ClassModel model) {
        super(model, 1, 2, 0);
        final List<Feature> features = model.getFeatures();
        final int numberOfFeatures = features.size();
        dataDependencies = new Set[numberOfFeatures];
        functionalDependencies = new Set[numberOfFeatures];
        isMethod = new boolean[numberOfFeatures];
        for (int i = 0; i < numberOfFeatures; i++) {
            if (features.get(i) instanceof Method method) {
                final Set<Integer> dataDependencies = new HashSet<>();
                final Set<Integer> functionalDependencies = new HashSet<>();
                for (final Attribute dataDependency : method.getDataDependency()) {
                    dataDependencies.add(features.indexOf(dataDependency));
                }
                for (final Method functionalDependency : method.getFunctionalDependency()) {
                    functionalDependencies.add(features.indexOf(functionalDependency));
                }
                this.dataDependencies[i] = dataDependencies;
                this.functionalDependencies[i] = functionalDependencies;
                isMethod[i] = true;
            }
        }
    }

    @Override
    public void evaluate(Solution solution) {
        final IntegerArray integerArray = (IntegerArray) solution.getVariable(0);
        final int[] features = integerArray.getArray();

        final int numberOfClasses = integerArray.getMax() + 1;

        final Set<Integer>[] encapsulatedAttributes = new Set[numberOfClasses];
        final Set<Integer>[] encapsulatedMethods = new Set[numberOfClasses];

        for (int i = 0; i < features.length; i++) {
            add(isMethod[i] ? encapsulatedMethods : encapsulatedAttributes, features[i], i);
        }

        double cohesionRatio = 0;
        double couplingRatio = 0;
        for (int c1 = 0; c1 < numberOfClasses; c1++) {
            final Set<Integer> encapsulatedAttributes1 = encapsulatedAttributes[c1];
            final Set<Integer> encapsulatedMethods1 = encapsulatedMethods[c1];
            cohesionRatio += cohesionRatio(encapsulatedAttributes1, encapsulatedMethods1);
            for (int c2 = 0; c2 < numberOfClasses; c2++) {
                if (c1 != c2) {
                    final Set<Integer> encapsulatedAttributes2 = encapsulatedAttributes[c2];
                    final Set<Integer> encapsulatedMethods2 = encapsulatedMethods[c2];
                    couplingRatio += couplingRatio(encapsulatedMethods1, encapsulatedAttributes2, encapsulatedMethods2);
                }
            }
        }
        solution.setObjective(0, -cohesionRatio);
        solution.setObjective(1, couplingRatio);
    }

    private static void add(Set<Integer>[] sets, int index, int value) {
        Set<Integer> set = sets[index];
        if (set != null) {
            set.add(value);
        } else {
            set = new HashSet<>();
            set.add(value);
            sets[index] = set;
        }
    }

    private double cohesionRatio(Set<Integer> encapsulatedAttributes, Set<Integer> encapsulatedMethods) {
        if (encapsulatedMethods == null) {
            // The class does not encapsulate any methods, so we immediately return 0
            return 0;
        } else if (encapsulatedMethods.size() == 1) {
            // The class only encapsulates one method, so we skip calculating MMI
            if (encapsulatedAttributes == null) {
                // The class does not encapsulate any attributes, so we skip calculating MAI
                return 0;
            }
            // The class encapsulates at least one attribute, so we return MAI
            return (double) mai(encapsulatedMethods, encapsulatedAttributes) / (encapsulatedMethods.size() * encapsulatedAttributes.size());
        }
        // The class encapsulates at least two methods
        if (encapsulatedAttributes == null) {
            // The class does not encapsulate any attributes, so we only return MMI
            return (double) mmi(encapsulatedMethods, encapsulatedMethods) / (encapsulatedMethods.size() * (encapsulatedMethods.size() - 1));
        }
        // The class encapsulates at least two methods and one attribute, so we return MAI + MMI
        return (double) mai(encapsulatedMethods, encapsulatedAttributes) / (encapsulatedMethods.size() * encapsulatedAttributes.size())
                + (double) mmi(encapsulatedMethods, encapsulatedMethods) / (encapsulatedMethods.size() * (encapsulatedMethods.size() - 1));
    }

    private double couplingRatio(Set<Integer> encapsulatedMethods1, Set<Integer> encapsulatedAttributes2, Set<Integer> encapsulatedMethods2) {
        if (encapsulatedMethods1 == null) {
            // The source class does not encapsulate any methods, so we immediately return 0
            return 0;
        }
        // The source class encapsulates at least one method
        if (encapsulatedMethods2 == null) {
            // The target class does not encapsulate any methods, so we skip calculating MMI
            if (encapsulatedAttributes2 == null) {
                // The target class does not encapsulate any attributes, so we skip calculating MAI
                return 0;
            }
            // The target class contains at least one attribute, so we return MAI
            return (double) mai(encapsulatedMethods1, encapsulatedAttributes2) / (encapsulatedMethods1.size() * encapsulatedAttributes2.size());
        }
        // The target class encapsulates at least one method
        if (encapsulatedAttributes2 == null) {
            // The target class encapsulates no attributes, so we return MMI
            return (double) mmi(encapsulatedMethods1, encapsulatedMethods2) / (encapsulatedMethods1.size() * encapsulatedMethods2.size());
        }
        // The target class encapsulates at least two methods and one attribute, so we return MAI + MMI
        return (double) mai(encapsulatedMethods1, encapsulatedAttributes2) / (encapsulatedMethods1.size() * encapsulatedAttributes2.size())
                + (double) mmi(encapsulatedMethods1, encapsulatedMethods2) / (encapsulatedMethods1.size() * encapsulatedMethods2.size());
    }

    private long mai(Set<Integer> encapsulatedMethods, Set<Integer> encapsulatedAttributes) {
        return encapsulatedMethods.stream()
                .map(i -> dataDependencies[i])
                .mapToLong(dataDependencies -> encapsulatedAttributes.stream()
                        .filter(dataDependencies::contains)
                        .count())
                .sum();
    }

    private long mmi(Set<Integer> encapsulatedMethods1, Set<Integer> encapsulatedMethods2) {
        return encapsulatedMethods1.stream()
                .map(i -> functionalDependencies[i])
                .mapToLong(functionalDependencies -> encapsulatedMethods2.stream()
                        .filter(functionalDependencies::contains)
                        .count())
                .sum();
    }

    @Override
    public Solution newSolution() {
        final Solution solution = new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints);

        final int numberOfFeatures = model.getFeatures().size();
        solution.setVariable(0, new IntegerArray(numberOfFeatures, 0, numberOfFeatures - 1));

        return solution;
    }

    public static NSGAII createAlgorithm(int run, ClassModel model) {
        final NSGAII nsgaii = new NSGAII(new TimingProblem(new CraIntArray(model)));
        final List<Operator> operators = List.of(new RandomSet(1.0 / model.getFeatures().size()));
        final Variation variation = new RandomMutation(operators);
        nsgaii.setVariation(variation);
        nsgaii.setInitialPopulationSize(40);
        return nsgaii;
    }
}
