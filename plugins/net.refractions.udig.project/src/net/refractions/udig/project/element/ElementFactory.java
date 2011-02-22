/**
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

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
