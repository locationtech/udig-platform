/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.util.List;

import net.refractions.udig.project.IProject;

import org.eclipse.emf.ecore.EObject;

/**
 * The read/write interface for IProject.
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface Project extends EObject, IProject {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Returns an unmodifiable
     */
    public <E> List<E> getElements( Class<E> type );

    /**
     * Returns a List with all elements in the project
     * THis list is modifiable.
     * 
     * @return a list with all in the project
     * @model type="ProjectElement" opposite="projectInternal"
     */
    public List<ProjectElement> getElementsInternal();

    /**
     * gets the name of the project
     * 
     * @return the name of the project
     * @uml.property name="name"
     * @model
     */
    public String getName();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.Project#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

}
