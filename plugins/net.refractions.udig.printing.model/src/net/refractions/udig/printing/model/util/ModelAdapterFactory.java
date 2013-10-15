/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.refractions.udig.printing.model.util;

import net.refractions.udig.printing.model.*;
import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Connection;
import net.refractions.udig.printing.model.Element;
import net.refractions.udig.printing.model.ModelPackage;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.internal.ProjectElement;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see net.refractions.udig.printing.model.ModelPackage
 * @generated
 */
public class ModelAdapterFactory extends AdapterFactoryImpl {
	/**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelPackage modelPackage;

	/**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ModelPackage.eINSTANCE;
        }
    }

	/**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

	/**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelSwitch<Adapter> modelSwitch =
		new ModelSwitch<Adapter>() {
            @Override
            public Adapter caseBox(Box object) {
                return createBoxAdapter();
            }
            @Override
            public Adapter caseConnection(Connection object) {
                return createConnectionAdapter();
            }
            @Override
            public Adapter caseElement(Element object) {
                return createElementAdapter();
            }
            @Override
            public Adapter casePage(Page object) {
                return createPageAdapter();
            }
            @Override
            public Adapter caseIProjectElement(IProjectElement object) {
                return createIProjectElementAdapter();
            }
            @Override
            public Adapter caseIAdaptable(IAdaptable object) {
                return createIAdaptableAdapter();
            }
            @Override
            public Adapter caseProjectElement(ProjectElement object) {
                return createProjectElementAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

	/**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.printing.model.Box <em>Box</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.printing.model.Box
     * @generated
     */
    public Adapter createBoxAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.printing.model.Connection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.printing.model.Connection
     * @generated
     */
    public Adapter createConnectionAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.printing.model.Element <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.printing.model.Element
     * @generated
     */
    public Adapter createElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.printing.model.Page <em>Page</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.printing.model.Page
     * @generated
     */
    public Adapter createPageAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.IProjectElement <em>IProject Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.IProjectElement
     * @generated
     */
    public Adapter createIProjectElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link org.eclipse.core.runtime.IAdaptable <em>IAdaptable</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.core.runtime.IAdaptable
     * @generated
     */
    public Adapter createIAdaptableAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for an object of class '{@link net.refractions.udig.project.internal.ProjectElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see net.refractions.udig.project.internal.ProjectElement
     * @generated
     */
    public Adapter createProjectElementAdapter() {
        return null;
    }

	/**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //ModelAdapterFactory
