/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;

import org.eclipse.emf.ecore.EObject;

/**
 * The EMF object extension to the IRenderer interface.  Non-udig developers should not have to reference this class.
 * 
 * @author Jesse
 * @since 1.0.0
 * @model abstract="true"
 */
public interface Renderer extends EObject, IRenderer {

    /**
     * Returns the current state of rendering.
     * <p>
     * The state is the current state of the {@linkplain org.eclipse.core.runtime.jobs.Job}
     * </p>
     * Options are:
     * <ul>
     * <li> {@linkplain #RENDERING} </li>
     * <li> {@linkplain #DONE} </li>
     * <li> {@linkplain #NEVER} </li>
     * <li> {@linkplain #DISPOSED} </li>
     * </ul>
     * 
     * @return the current state of rendering.
     * @uml.property name="state"
     * @model default="0"
     */
    public int getState();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.Renderer#getState <em>State</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>State</em>' attribute.
     * @see #getState()
     * @generated
     */
    void setState( int value );

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getRenderer_Name()
     * @model id="true"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.Renderer#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * Returns the value of the '<em><b>Toolkit</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Toolkit</em>' attribute isn't clear, there really should be
     * more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Toolkit</em>' attribute.
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getRenderer_Context()
     * @model transient="true" resolveProxies="false"
     */
    IRenderContext getContext();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.Renderer#getContext <em>Context</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Context</em>' reference.
     * @see #getContext()
     * @generated
     */
    void setContext( IRenderContext value );

    /**
     * Informs the renderer to dispose of resources
     * 
     * @model
     */
    public void dispose();

}
