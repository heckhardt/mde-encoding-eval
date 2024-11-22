/**
 */
package io.github.sekassel.moea.model.knapsack;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link KnapsackModel#getKnapsacks <em>Knapsacks</em>}</li>
 *   <li>{@link KnapsackModel#getItems <em>Items</em>}</li>
 * </ul>
 *
 * @see KnapsackPackage#getKnapsackModel()
 * @model
 * @generated
 */
public interface KnapsackModel extends EObject {
	/**
	 * Returns the value of the '<em><b>Knapsacks</b></em>' containment reference list.
	 * The list contents are of type {@link Knapsack}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Knapsacks</em>' containment reference list.
	 * @see KnapsackPackage#getKnapsackModel_Knapsacks()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Knapsack> getKnapsacks();

	/**
	 * Returns the value of the '<em><b>Items</b></em>' containment reference list.
	 * The list contents are of type {@link Item}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Items</em>' containment reference list.
	 * @see KnapsackPackage#getKnapsackModel_Items()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Item> getItems();

} // KnapsackModel
