/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see net.refractions.udig.printing.model.ModelPackage
 * @generated
 */
public interface ModelFactory extends EFactory{
	/**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelFactory eINSTANCE = net.refractions.udig.printing.model.impl.ModelFactoryImpl.init();

	/**
     * Returns a new object of class '<em>Box</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Box</em>'.
     * @generated
     */
    Box createBox();

	/**
     * Returns a new object of class '<em>Connection</em>'.
     * <!-- begin-user-doc -->
	 * @see Connection
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Connection</em>'.
     * @generated
     */
    Connection createConnection();

	/**
     * Returns a new object of class '<em>Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Element</em>'.
     * @generated
     */
    Element createElement();

	/**
     * Returns a new object of class '<em>Page</em>'.
     * <!-- begin-user-doc -->
	 * @see Page
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Page</em>'.
     * @generated
     */
    Page createPage();

	/**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
	 * @see ModelPackage
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelPackage getModelPackage();

} //ModelFactory
