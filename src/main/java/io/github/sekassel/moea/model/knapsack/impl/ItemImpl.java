/**
 */
package io.github.sekassel.moea.model.knapsack.impl;

import io.github.sekassel.moea.model.knapsack.Item;
import io.github.sekassel.moea.model.knapsack.Knapsack;
import io.github.sekassel.moea.model.knapsack.KnapsackPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link ItemImpl#getIsContainedBy <em>Is Contained By</em>}</li>
 *   <li>{@link ItemImpl#getWeights <em>Weights</em>}</li>
 *   <li>{@link ItemImpl#getValues <em>Values</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ItemImpl extends MinimalEObjectImpl.Container implements Item {
	/**
	 * The cached value of the '{@link #getIsContainedBy() <em>Is Contained By</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsContainedBy()
	 * @generated
	 * @ordered
	 */
	protected Knapsack isContainedBy;

	/**
	 * The cached value of the '{@link #getWeights() <em>Weights</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWeights()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> weights;

	/**
	 * The cached value of the '{@link #getValues() <em>Values</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValues()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> values;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ItemImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return KnapsackPackage.Literals.ITEM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Knapsack getIsContainedBy() {
		if (isContainedBy != null && isContainedBy.eIsProxy()) {
			InternalEObject oldIsContainedBy = (InternalEObject)isContainedBy;
			isContainedBy = (Knapsack)eResolveProxy(oldIsContainedBy);
			if (isContainedBy != oldIsContainedBy) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, KnapsackPackage.ITEM__IS_CONTAINED_BY, oldIsContainedBy, isContainedBy));
			}
		}
		return isContainedBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Knapsack basicGetIsContainedBy() {
		return isContainedBy;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIsContainedBy(Knapsack newIsContainedBy, NotificationChain msgs) {
		Knapsack oldIsContainedBy = isContainedBy;
		isContainedBy = newIsContainedBy;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, KnapsackPackage.ITEM__IS_CONTAINED_BY, oldIsContainedBy, newIsContainedBy);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIsContainedBy(Knapsack newIsContainedBy) {
		if (newIsContainedBy != isContainedBy) {
			NotificationChain msgs = null;
			if (isContainedBy != null)
				msgs = ((InternalEObject)isContainedBy).eInverseRemove(this, KnapsackPackage.KNAPSACK__CONTAINS, Knapsack.class, msgs);
			if (newIsContainedBy != null)
				msgs = ((InternalEObject)newIsContainedBy).eInverseAdd(this, KnapsackPackage.KNAPSACK__CONTAINS, Knapsack.class, msgs);
			msgs = basicSetIsContainedBy(newIsContainedBy, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, KnapsackPackage.ITEM__IS_CONTAINED_BY, newIsContainedBy, newIsContainedBy));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Integer> getWeights() {
		if (weights == null) {
			weights = new EDataTypeEList<Integer>(Integer.class, this, KnapsackPackage.ITEM__WEIGHTS);
		}
		return weights;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Integer> getValues() {
		if (values == null) {
			values = new EDataTypeEList<Integer>(Integer.class, this, KnapsackPackage.ITEM__VALUES);
		}
		return values;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				if (isContainedBy != null)
					msgs = ((InternalEObject)isContainedBy).eInverseRemove(this, KnapsackPackage.KNAPSACK__CONTAINS, Knapsack.class, msgs);
				return basicSetIsContainedBy((Knapsack)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				return basicSetIsContainedBy(null, msgs);
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
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				if (resolve) return getIsContainedBy();
				return basicGetIsContainedBy();
			case KnapsackPackage.ITEM__WEIGHTS:
				return getWeights();
			case KnapsackPackage.ITEM__VALUES:
				return getValues();
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
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				setIsContainedBy((Knapsack)newValue);
				return;
			case KnapsackPackage.ITEM__WEIGHTS:
				getWeights().clear();
				getWeights().addAll((Collection<? extends Integer>)newValue);
				return;
			case KnapsackPackage.ITEM__VALUES:
				getValues().clear();
				getValues().addAll((Collection<? extends Integer>)newValue);
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
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				setIsContainedBy((Knapsack)null);
				return;
			case KnapsackPackage.ITEM__WEIGHTS:
				getWeights().clear();
				return;
			case KnapsackPackage.ITEM__VALUES:
				getValues().clear();
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
			case KnapsackPackage.ITEM__IS_CONTAINED_BY:
				return isContainedBy != null;
			case KnapsackPackage.ITEM__WEIGHTS:
				return weights != null && !weights.isEmpty();
			case KnapsackPackage.ITEM__VALUES:
				return values != null && !values.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (weights: ");
		result.append(weights);
		result.append(", values: ");
		result.append(values);
		result.append(')');
		return result.toString();
	}

} //ItemImpl
