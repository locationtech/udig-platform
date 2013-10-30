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
package net.refractions.udig.project.element;

import net.refractions.udig.project.IProject;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see net.refractions.udig.project.element.ElementPackage
 * @generated
 */
public interface ElementFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ElementFactory eINSTANCE = net.refractions.udig.project.element.impl.ElementFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Project Element Adapter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Project Element Adapter</em>'.
     * @generated
     */
    ProjectElementAdapter createProjectElementAdapter();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ElementPackage getElementPackage();

    /**
     * Creates a {@link IGenericProjectElement} with an adapter that allows it to integrate with the 
     * ProjectElement framework.  
     *
     * @param project the project to add the new ProjectElementAdapter ot
     * @param typeToCreate the type of {@link IGenericProjectElement} that is created by the extension
     * @param extensionId The id of the extension to use to create the IGenericProjectElement
     * 
     * @return the new {@link ProjectElementAdapter} (it has been added to the project already)
     */
    ProjectElementAdapter createProjectElementAdapter( IProject project,
            Class< ? extends IGenericProjectElement> typeToCreate, String extensionId );

    /**
     * Creates a {@link IGenericProjectElement} with an adapter that allows it to integrate with the 
     * ProjectElement framework.  
     *
     * @param project the project to add the new ProjectElementAdapter to
     * @param elemName the name of the ProjectElementAdapter.  It will also be the name of the eResource created.
     * @param typeToCreate the type of {@link IGenericProjectElement} that is created by the extension
     * @param extensionId The id of the extension to use to create the IGenericProjectElement
     * 
     * @return the new {@link ProjectElementAdapter} (it has been added to the project already)
     */
    ProjectElementAdapter createProjectElementAdapter( IProject project, String elemName,
            Class< ? extends IGenericProjectElement> typeToCreate, String extensionId );

} // ElementFactory
