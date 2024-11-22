/**
 */
package io.github.sekassel.moea.model.knapsack.impl;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackModel;
import io.github.sekassel.moea.model.knapsack.KnapsackPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link KnapsackModelImpl#getKnapsacks <em>Knapsacks</em>}</li>
 *   <li>{@link KnapsackModelImpl#getItems <em>Items</em>}</li>
 * </ul>
 *
 * @generated
 */
public class KnapsackModelImpl extends MinimalEObjectImpl.Container implements KnapsackModel {
	/**
	 * The cached value of the '{@link #getKnapsacks() <em>Knapsacks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKnapsacks()
	 * @generated
	 * @ordered
	 */
	protected EList<Knapsack> knapsacks;

	/**
	 * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getItems()
	 * @generated
	 * @ordered
	 */
	protected EList<Item> items;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected KnapsackModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return KnapsackPackage.Literals.KNAPSACK_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Knapsack> getKnapsacks() {
		if (knapsacks == null) {
			knapsacks = new EObjectContainmentEList<Knapsack>(Knapsack.class, this, KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS);
		}
		return knapsacks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Item> getItems() {
		if (items == null) {
			items = new EObjectContainmentEList<Item>(Item.class, this, KnapsackPackage.KNAPSACK_MODEL__ITEMS);
		}
		return items;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS:
				return ((InternalEList<?>)getKnapsacks()).basicRemove(otherEnd, msgs);
			case KnapsackPackage.KNAPSACK_MODEL__ITEMS:
				return ((InternalEList<?>)getItems()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS:
				return getKnapsacks();
			case KnapsackPackage.KNAPSACK_MODEL__ITEMS:
				return getItems();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS:
				getKnapsacks().clear();
				getKnapsacks().addAll((Collection<? extends Knapsack>)newValue);
				return;
			case KnapsackPackage.KNAPSACK_MODEL__ITEMS:
				getItems().clear();
				getItems().addAll((Collection<? extends Item>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS:
				getKnapsacks().clear();
				return;
			case KnapsackPackage.KNAPSACK_MODEL__ITEMS:
				getItems().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case KnapsackPackage.KNAPSACK_MODEL__KNAPSACKS:
				return knapsacks != null && !knapsacks.isEmpty();
			case KnapsackPackage.KNAPSACK_MODEL__ITEMS:
				return items != null && !items.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //KnapsackModelImpl
