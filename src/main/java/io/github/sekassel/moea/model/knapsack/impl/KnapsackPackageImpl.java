/**
 */
package io.github.sekassel.moea.model.knapsack.impl;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackFactory;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.model.knapsack.KnapsackPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class KnapsackPackageImpl extends EPackageImpl implements KnapsackPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass knapsackModelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass knapsackEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass itemEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see Registry
	 * @see KnapsackPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private KnapsackPackageImpl() {
		super(eNS_URI, KnapsackFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link KnapsackPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static KnapsackPackage init() {
		if (isInited) return (KnapsackPackage) Registry.INSTANCE.getEPackage(KnapsackPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredKnapsackPackage = Registry.INSTANCE.get(eNS_URI);
		KnapsackPackageImpl theKnapsackPackage = registeredKnapsackPackage instanceof KnapsackPackageImpl ? (KnapsackPackageImpl)registeredKnapsackPackage : new KnapsackPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theKnapsackPackage.createPackageContents();

		// Initialize created meta-data
		theKnapsackPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theKnapsackPackage.freeze();

		// Update the registry and return the package
		Registry.INSTANCE.put(KnapsackPackage.eNS_URI, theKnapsackPackage);
		return theKnapsackPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getKnapsackModel() {
		return knapsackModelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getKnapsackModel_Knapsacks() {
		return (EReference)knapsackModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getKnapsackModel_Items() {
		return (EReference)knapsackModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getKnapsack() {
		return knapsackEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getKnapsack_Capacity() {
		return (EAttribute)knapsackEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getKnapsack_Contains() {
		return (EReference)knapsackEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getItem() {
		return itemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getItem_IsContainedBy() {
		return (EReference)itemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getItem_Weights() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getItem_Values() {
		return (EAttribute)itemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public KnapsackFactory getKnapsackFactory() {
		return (KnapsackFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		knapsackModelEClass = createEClass(KNAPSACK_MODEL);
		createEReference(knapsackModelEClass, KNAPSACK_MODEL__KNAPSACKS);
		createEReference(knapsackModelEClass, KNAPSACK_MODEL__ITEMS);

		knapsackEClass = createEClass(KNAPSACK);
		createEAttribute(knapsackEClass, KNAPSACK__CAPACITY);
		createEReference(knapsackEClass, KNAPSACK__CONTAINS);

		itemEClass = createEClass(ITEM);
		createEReference(itemEClass, ITEM__IS_CONTAINED_BY);
		createEAttribute(itemEClass, ITEM__WEIGHTS);
		createEAttribute(itemEClass, ITEM__VALUES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(knapsackModelEClass, KnapsackModel.class, "KnapsackModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getKnapsackModel_Knapsacks(), this.getKnapsack(), null, "knapsacks", null, 1, -1, KnapsackModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getKnapsackModel_Items(), this.getItem(), null, "items", null, 1, -1, KnapsackModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(knapsackEClass, Knapsack.class, "Knapsack", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getKnapsack_Capacity(), ecorePackage.getEInt(), "capacity", null, 0, 1, Knapsack.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getKnapsack_Contains(), this.getItem(), this.getItem_IsContainedBy(), "contains", null, 0, -1, Knapsack.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(itemEClass, Item.class, "Item", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getItem_IsContainedBy(), this.getKnapsack(), this.getKnapsack_Contains(), "isContainedBy", null, 0, 1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getItem_Weights(), ecorePackage.getEInt(), "weights", null, 1, -1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getItem_Values(), ecorePackage.getEInt(), "values", null, 1, -1, Item.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //KnapsackPackageImpl
