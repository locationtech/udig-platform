/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.render.IRenderManager;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.emf.ecore.EObject;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of net.refractions.udig.project.internal.render
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface RenderManager extends EObject, IRenderManager {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    String copyright = "uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details."; //$NON-NLS-1$

    /**
     * Returns the Map associated with the current renderManager.
     *
     * @return the Map associated with the current renderManager.
     * @model many="false" opposite="renderManagerInternal" transient="true"
     */
    public Map getMapInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderManager#getMapInternal <em>Map Internal</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Map Internal</em>' reference.
     * @see #getMapInternal()
     * @generated
     */
    void setMapInternal( Map value );

    /**
     * Returns the value of the '<em><b>Render Executor</b></em>' reference. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Render Executor</em>' reference isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Render Executor</em>' reference.
     * @see #setRenderExecutor(RenderExecutor)
     * @see net.refractions.udig.project.internal.render.RenderPackage#getRenderManager_RenderExecutor()
     * @model resolveProxies="false"
     * @generated
     */
    RenderExecutor getRenderExecutor();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderManager#getRenderExecutor <em>Render Executor</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Render Executor</em>' reference.
     * @see #getRenderExecutor()
     * @generated
     */
    void setRenderExecutor( RenderExecutor value );

    /**
     * Gets the ViewportPane for the current RenderManager.
     *
     * @return the ViewportPane for the current RenderManager
     * @model many="false" dataType=net.refractions.udig.project.render.displayAdapter.IMapDisplay"
     */
    public IMapDisplay getMapDisplay();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderManager#getMapDisplay <em>Map Display</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Map Display</em>' attribute.
     * @see #getMapDisplay()
     * @generated
     */
    void setMapDisplay( IMapDisplay value );

    /**
     * Forces the area in all layers to be re-rendered. If bounds is null then the entire layer must
     * be rendered.
     *
     * @model
     */
    public void refresh( Envelope bounds );

    /**
     * Returns the RendererCreator used to create renderers.
     *
     * @return the RendererCreator used to create renderers.
     */
    public RendererCreator getRendererCreator();

    /**
     * Returns the value of the '<em><b>Viewport Model Internal</b></em>' reference. It is
     * bidirectional and its opposite is '{@link net.refractions.udig.project.internal.render.ViewportModel#getRenderManagerInternal <em>Render Manager Internal</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Viewport Model</em>' reference isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     *
     * @return the value of the '<em>Viewport Model Internal</em>' reference.
     * @see #setViewportModelInternal(ViewportModel)
     * @see net.refractions.udig.project.internal.render.RenderPackage#getRenderManager_ViewportModelInternal()
     * @see net.refractions.udig.project.internal.render.ViewportModel#getRenderManagerInternal
     * @model opposite="renderManagerInternal" resolveProxies="false" transient="true"
     * @generated
     */
    ViewportModel getViewportModelInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderManager#getViewportModelInternal <em>Viewport Model Internal</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Viewport Model Internal</em>' reference.
     * @see #getViewportModelInternal()
     * @generated
     */
    void setViewportModelInternal( ViewportModel value );

    /**
     * dispose held resources.
     *
     * @model
     */
    public void dispose();

    /**
     * Returns true if the RenderManager has been disposed.
     *
     * @return
     */
    public boolean isDisposed();

    /**
     * Disables rendering until {@link #enableRendering()} is called
     */
	public void disableRendering();

	/**
	 * Enables rendering, has no effect if already enabled.
	 */
	public void enableRendering();

	/**
	 * Returns whether rendering is permitted
	 *
	 * @return true if rendering is permitted
	 */
	public boolean isRenderingEnabled();

}
