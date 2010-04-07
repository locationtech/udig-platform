/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderer;

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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

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
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.Renderer#getState <em>State</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>State</em>' attribute.
     * @see #getState()
     * @generated
     */
    void setState( int value );

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see net.refractions.udig.project.internal.render.RenderPackage#getRenderer_Name()
     * @model id="true"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.Renderer#getName <em>Name</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
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
     * @see net.refractions.udig.project.internal.render.RenderPackage#getRenderer_Context()
     * @model transient="true" resolveProxies="false"
     */
    IRenderContext getContext();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.Renderer#getContext <em>Context</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Context</em>' reference.
     * @see #getContext()
     * @generated
     */
    void setContext( IRenderContext context );

    /**
     * Informs the renderer to dispose of resources
     * 
     * @model
     */
    public void dispose();

}
