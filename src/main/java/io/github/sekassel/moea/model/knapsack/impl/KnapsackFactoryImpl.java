/**
 */
package io.github.sekassel.moea.model.knapsack.impl;

import io.github.sekassel.moea.model.knapsack.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class KnapsackFactoryImpl extends EFactoryImpl implements KnapsackFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static KnapsackFactory init() {
		try {
			KnapsackFactory theKnapsackFactory = (KnapsackFactory)EPackage.Registry.INSTANCE.getEFactory(KnapsackPackage.eNS_URI);
			if (theKnapsackFactory != null) {
				return theKnapsackFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new KnapsackFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public KnapsackFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case KnapsackPackage.KNAPSACK_MODEL: return createKnapsackModel();
			case KnapsackPackage.KNAPSACK: return createKnapsack();
			case KnapsackPackage.ITEM: return createItem();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public KnapsackModel createKnapsackModel() {
		KnapsackModelImpl knapsackModel = new KnapsackModelImpl();
		return knapsackModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Knapsack createKnapsack() {
		KnapsackImpl knapsack = new KnapsackImpl();
		return knapsack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Item createItem() {
		ItemImpl item = new ItemImpl();
		return item;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public KnapsackPackage getKnapsackPackage() {
		return (KnapsackPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static KnapsackPackage getPackage() {
		return KnapsackPackage.eINSTANCE;
	}

} //KnapsackFactoryImpl
