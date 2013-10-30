/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
