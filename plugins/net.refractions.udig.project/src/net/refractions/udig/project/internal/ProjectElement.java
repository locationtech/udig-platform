/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.IProjectElement;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;

/**
 * The interface that describes an Element that can be part of a Project.
 * @author Jesse
 * @since 1.0.0
 * @model interface="true"
 */
public interface ProjectElement extends EObject, IProjectElement, IAdaptable {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Returns the owner project of Map.
     * 
     * @return the owner project of Map.
     * @model many="false" opposite="elementsInternal"
     */
    public Project getProjectInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.ProjectElement#getProjectInternal <em>Project Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Project Internal</em>' reference.
     * @see #getProjectInternal()
     * @generated
     */
    void setProjectInternal( Project value );

    /**
     * Returns the owner project of Map.
     * 
     * @return the owner project of Map.
     * @uml.property name="name"
     * @model many="false"
     */
    public String getName();

    /**
     * The new name for the project element.
     * 
     * @param name
     * @uml.property name="name"
     */
    public void setName( String name );

    /**
     * Returns the default file extension for this type of project element.
     * @return
     */
    public String getFileExtension();

}
