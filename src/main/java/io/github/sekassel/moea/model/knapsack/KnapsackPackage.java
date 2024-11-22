/**
 */
package io.github.sekassel.moea.model.knapsack;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see KnapsackFactory
 * @model kind="package"
 * @generated
 */
public interface KnapsackPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "knapsack";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://sekassel.github.io/moea/model/knapsack";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "knapsack";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	KnapsackPackage eINSTANCE = io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl.init();

	/**
	 * The meta object id for the '{@link io.github.sekassel.moea.model.knapsack.impl.KnapsackModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackModelImpl
	 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getKnapsackModel()
	 * @generated
	 */
	int KNAPSACK_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Knapsacks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_MODEL__KNAPSACKS = 0;

	/**
	 * The feature id for the '<em><b>Items</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_MODEL__ITEMS = 1;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_MODEL_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_MODEL_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link io.github.sekassel.moea.model.knapsack.impl.KnapsackImpl <em>Knapsack</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackImpl
	 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getKnapsack()
	 * @generated
	 */
	int KNAPSACK = 1;

	/**
	 * The feature id for the '<em><b>Capacity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK__CAPACITY = 0;

	/**
	 * The feature id for the '<em><b>Contains</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK__CONTAINS = 1;

	/**
	 * The number of structural features of the '<em>Knapsack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Knapsack</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KNAPSACK_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link io.github.sekassel.moea.model.knapsack.impl.ItemImpl <em>Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see io.github.sekassel.moea.model.knapsack.impl.ItemImpl
	 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getItem()
	 * @generated
	 */
	int ITEM = 2;

	/**
	 * The feature id for the '<em><b>Is Contained By</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__IS_CONTAINED_BY = 0;

	/**
	 * The feature id for the '<em><b>Weights</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__WEIGHTS = 1;

	/**
	 * The feature id for the '<em><b>Values</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM__VALUES = 2;

	/**
	 * The number of structural features of the '<em>Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ITEM_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link KnapsackModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see KnapsackModel
	 * @generated
	 */
	EClass getKnapsackModel();

	/**
	 * Returns the meta object for the containment reference list '{@link KnapsackModel#getKnapsacks <em>Knapsacks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Knapsacks</em>'.
	 * @see KnapsackModel#getKnapsacks()
	 * @see #getKnapsackModel()
	 * @generated
	 */
	EReference getKnapsackModel_Knapsacks();

	/**
	 * Returns the meta object for the containment reference list '{@link KnapsackModel#getItems <em>Items</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Items</em>'.
	 * @see KnapsackModel#getItems()
	 * @see #getKnapsackModel()
	 * @generated
	 */
	EReference getKnapsackModel_Items();

	/**
	 * Returns the meta object for class '{@link Knapsack <em>Knapsack</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Knapsack</em>'.
	 * @see Knapsack
	 * @generated
	 */
	EClass getKnapsack();

	/**
	 * Returns the meta object for the attribute '{@link Knapsack#getCapacity <em>Capacity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Capacity</em>'.
	 * @see Knapsack#getCapacity()
	 * @see #getKnapsack()
	 * @generated
	 */
	EAttribute getKnapsack_Capacity();

	/**
	 * Returns the meta object for the reference list '{@link Knapsack#getContains <em>Contains</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Contains</em>'.
	 * @see Knapsack#getContains()
	 * @see #getKnapsack()
	 * @generated
	 */
	EReference getKnapsack_Contains();

	/**
	 * Returns the meta object for class '{@link Item <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Item</em>'.
	 * @see Item
	 * @generated
	 */
	EClass getItem();

	/**
	 * Returns the meta object for the reference '{@link Item#getIsContainedBy <em>Is Contained By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Is Contained By</em>'.
	 * @see Item#getIsContainedBy()
	 * @see #getItem()
	 * @generated
	 */
	EReference getItem_IsContainedBy();

	/**
	 * Returns the meta object for the attribute list '{@link Item#getWeights <em>Weights</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Weights</em>'.
	 * @see Item#getWeights()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Weights();

	/**
	 * Returns the meta object for the attribute list '{@link Item#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Values</em>'.
	 * @see Item#getValues()
	 * @see #getItem()
	 * @generated
	 */
	EAttribute getItem_Values();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	KnapsackFactory getKnapsackFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link io.github.sekassel.moea.model.knapsack.impl.KnapsackModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackModelImpl
		 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getKnapsackModel()
		 * @generated
		 */
		EClass KNAPSACK_MODEL = eINSTANCE.getKnapsackModel();

		/**
		 * The meta object literal for the '<em><b>Knapsacks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference KNAPSACK_MODEL__KNAPSACKS = eINSTANCE.getKnapsackModel_Knapsacks();

		/**
		 * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference KNAPSACK_MODEL__ITEMS = eINSTANCE.getKnapsackModel_Items();

		/**
		 * The meta object literal for the '{@link io.github.sekassel.moea.model.knapsack.impl.KnapsackImpl <em>Knapsack</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackImpl
		 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getKnapsack()
		 * @generated
		 */
		EClass KNAPSACK = eINSTANCE.getKnapsack();

		/**
		 * The meta object literal for the '<em><b>Capacity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute KNAPSACK__CAPACITY = eINSTANCE.getKnapsack_Capacity();

		/**
		 * The meta object literal for the '<em><b>Contains</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference KNAPSACK__CONTAINS = eINSTANCE.getKnapsack_Contains();

		/**
		 * The meta object literal for the '{@link io.github.sekassel.moea.model.knapsack.impl.ItemImpl <em>Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see io.github.sekassel.moea.model.knapsack.impl.ItemImpl
		 * @see io.github.sekassel.moea.model.knapsack.impl.KnapsackPackageImpl#getItem()
		 * @generated
		 */
		EClass ITEM = eINSTANCE.getItem();

		/**
		 * The meta object literal for the '<em><b>Is Contained By</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ITEM__IS_CONTAINED_BY = eINSTANCE.getItem_IsContainedBy();

		/**
		 * The meta object literal for the '<em><b>Weights</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__WEIGHTS = eINSTANCE.getItem_Weights();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ITEM__VALUES = eINSTANCE.getItem_Values();

	}

} //KnapsackPackage
