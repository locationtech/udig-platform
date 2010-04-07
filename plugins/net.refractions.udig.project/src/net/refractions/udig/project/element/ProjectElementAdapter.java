/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.element;

import net.refractions.udig.project.internal.ProjectElement;

/**
 * Adapts a {@link IGenericProjectElement} to the ProjectElement interface.  This is
 * to provide a way to easily add objects to a project.
 * 
 * @see IGenericProjectElement
 * @see net.refractions.udig.project.element extension point
 * 
 * @author jesse
 * @model
 */
public interface ProjectElementAdapter extends ProjectElement {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$
    String EXT_ID = "net.refractions.udig.project.element"; //$NON-NLS-1$

    /**
     * Returns the object that is persisted and wrapped by this class
     * 
     * @return the object that is persisted and wrapped by this class
     * @model
     */
    public IGenericProjectElement getBackingObject();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.element.ProjectElementAdapter#getBackingObject <em>Backing Object</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Backing Object</em>' attribute.
     * @see #getBackingObject()
     * @generated
     */
    void setBackingObject( IGenericProjectElement value );

}
