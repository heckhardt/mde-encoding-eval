/**
 */
package io.github.sekassel.moea.model.cra;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Class#getEncapsulates <em>Encapsulates</em>}</li>
 * </ul>
 *
 * @see CraPackage#getClass_()
 * @model
 * @generated
 */
public interface Class extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Encapsulates</b></em>' reference list.
	 * The list contents are of type {@link Feature}.
	 * It is bidirectional and its opposite is '{@link Feature#getIsEncapsulatedBy <em>Is Encapsulated By</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Encapsulates</em>' reference list.
	 * @see CraPackage#getClass_Encapsulates()
	 * @see Feature#getIsEncapsulatedBy
	 * @model opposite="isEncapsulatedBy" required="true"
	 * @generated
	 */
	EList<Feature> getEncapsulates();

} // Class
