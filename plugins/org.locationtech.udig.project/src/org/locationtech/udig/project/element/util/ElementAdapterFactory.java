/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 * $Id$
 */
package org.locationtech.udig.project.element.util;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.locationtech.udig.project.IProjectElement;
import org.locationtech.udig.project.element.ElementPackage;
import org.locationtech.udig.project.element.ProjectElementAdapter;
import org.locationtech.udig.project.internal.ProjectElement;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.locationtech.udig.project.element.ElementPackage
 * @generated
 */
public class ElementAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ElementPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ElementAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ElementPackage.eINSTANCE;
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
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ElementSwitch<Adapter> modelSwitch = new ElementSwitch<Adapter>() {
        @Override
        public Adapter caseProjectElementAdapter(ProjectElementAdapter object) {
            return createProjectElementAdapterAdapter();
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
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.element.ProjectElementAdapter <em>Project Element Adapter</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.element.ProjectElementAdapter
     * @generated
     */
    public Adapter createProjectElementAdapterAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.IProjectElement <em>IProject Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.IProjectElement
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
     * Creates a new adapter for an object of class '{@link org.locationtech.udig.project.internal.ProjectElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.locationtech.udig.project.internal.ProjectElement
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

} //ElementAdapterFactory
