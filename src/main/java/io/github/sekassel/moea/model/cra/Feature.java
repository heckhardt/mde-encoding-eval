/**
 */
package io.github.sekassel.moea.model.cra;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Feature#getIsEncapsulatedBy <em>Is Encapsulated By</em>}</li>
 * </ul>
 *
 * @see CraPackage#getFeature()
 * @model abstract="true"
 * @generated
 */
public interface Feature extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Is Encapsulated By</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link Class#getEncapsulates <em>Encapsulates</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Encapsulated By</em>' reference.
	 * @see #setIsEncapsulatedBy(Class)
	 * @see CraPackage#getFeature_IsEncapsulatedBy()
	 * @see Class#getEncapsulates
	 * @model opposite="encapsulates"
	 * @generated
	 */
	Class getIsEncapsulatedBy();

	/**
	 * Sets the value of the '{@link Feature#getIsEncapsulatedBy <em>Is Encapsulated By</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Encapsulated By</em>' reference.
	 * @see #getIsEncapsulatedBy()
	 * @generated
	 */
	void setIsEncapsulatedBy(Class value);

} // Feature
