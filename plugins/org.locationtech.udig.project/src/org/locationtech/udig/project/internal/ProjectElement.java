/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import org.locationtech.udig.project.IProjectElement;

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
     * Returns the owner project of Map.
     * 
     * @return the owner project of Map.
     * @model many="false" opposite="elementsInternal"
     */
    public Project getProjectInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.ProjectElement#getProjectInternal <em>Project Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Project Internal</em>' reference.
     * @see #getProjectInternal()
     * @generated
     */
    void setProjectInternal(Project value);

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
    public void setName(String name);

    /**
     * Returns the default file extension for this type of project element.
     * @return
     */
    public String getFileExtension();

}
