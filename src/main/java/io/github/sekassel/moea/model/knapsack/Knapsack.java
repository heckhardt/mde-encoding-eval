/**
 */
package io.github.sekassel.moea.model.knapsack;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Knapsack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Knapsack#getCapacity <em>Capacity</em>}</li>
 *   <li>{@link Knapsack#getContains <em>Contains</em>}</li>
 * </ul>
 *
 * @see KnapsackPackage#getKnapsack()
 * @model
 * @generated
 */
public interface Knapsack extends EObject {
	/**
	 * Returns the value of the '<em><b>Capacity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Capacity</em>' attribute.
	 * @see #setCapacity(int)
	 * @see KnapsackPackage#getKnapsack_Capacity()
	 * @model
	 * @generated
	 */
	int getCapacity();

	/**
	 * Sets the value of the '{@link Knapsack#getCapacity <em>Capacity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Capacity</em>' attribute.
	 * @see #getCapacity()
	 * @generated
	 */
	void setCapacity(int value);

	/**
	 * Returns the value of the '<em><b>Contains</b></em>' reference list.
	 * The list contents are of type {@link Item}.
	 * It is bidirectional and its opposite is '{@link Item#getIsContainedBy <em>Is Contained By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contains</em>' reference list.
	 * @see KnapsackPackage#getKnapsack_Contains()
	 * @see Item#getIsContainedBy
	 * @model opposite="isContainedBy"
	 * @generated
	 */
	EList<Item> getContains();

} // Knapsack
