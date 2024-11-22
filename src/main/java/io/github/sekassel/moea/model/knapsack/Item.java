/**
 */
package io.github.sekassel.moea.model.knapsack;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Item#getIsContainedBy <em>Is Contained By</em>}</li>
 *   <li>{@link Item#getWeights <em>Weights</em>}</li>
 *   <li>{@link Item#getValues <em>Values</em>}</li>
 * </ul>
 *
 * @see KnapsackPackage#getItem()
 * @model
 * @generated
 */
public interface Item extends EObject {
	/**
	 * Returns the value of the '<em><b>Is Contained By</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link Knapsack#getContains <em>Contains</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Contained By</em>' reference.
	 * @see #setIsContainedBy(Knapsack)
	 * @see KnapsackPackage#getItem_IsContainedBy()
	 * @see Knapsack#getContains
	 * @model opposite="contains"
	 * @generated
	 */
	Knapsack getIsContainedBy();

	/**
	 * Sets the value of the '{@link Item#getIsContainedBy <em>Is Contained By</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Contained By</em>' reference.
	 * @see #getIsContainedBy()
	 * @generated
	 */
	void setIsContainedBy(Knapsack value);

	/**
	 * Returns the value of the '<em><b>Weights</b></em>' attribute list.
	 * The list contents are of type {@link Integer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Weights</em>' attribute list.
	 * @see KnapsackPackage#getItem_Weights()
	 * @model unique="false" required="true"
	 * @generated
	 */
	EList<Integer> getWeights();

	/**
	 * Returns the value of the '<em><b>Values</b></em>' attribute list.
	 * The list contents are of type {@link Integer}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Values</em>' attribute list.
	 * @see KnapsackPackage#getItem_Values()
	 * @model unique="false" required="true"
	 * @generated
	 */
	EList<Integer> getValues();

} // Item
