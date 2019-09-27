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

import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.eclipse.emf.ecore.EObject;

import org.locationtech.jts.geom.Envelope;

/**
 * 
 *  Responsible for creating renderers and triggering refreshes.
 * 
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 * @model
 */
public interface RenderManager extends EObject, IRenderManager {
    /**
     * Returns the Map associated with the current renderManager.
     * 
     * @return the Map associated with the current renderManager.
     * @model many="false" opposite="renderManagerInternal" transient="true"
     */
    public Map getMapInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RenderManager#getMapInternal <em>Map Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map Internal</em>' reference.
     * @see #getMapInternal()
     * @generated
     */
    void setMapInternal(Map value);

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
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getRenderManager_RenderExecutor()
     * @model resolveProxies="false"
     * @generated
     */
    RenderExecutor getRenderExecutor();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RenderManager#getRenderExecutor <em>Render Executor</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Render Executor</em>' reference.
     * @see #getRenderExecutor()
     * @generated
     */
    void setRenderExecutor(RenderExecutor value);

    /**
     * Gets the ViewportPane for the current RenderManager.
     * 
     * @return the ViewportPane for the current RenderManager
     * @model many="false" dataType=org.locationtech.udig.project.render.displayAdapter.IMapDisplay"
     */
    public IMapDisplay getMapDisplay();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RenderManager#getMapDisplay <em>Map Display</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Map Display</em>' attribute.
     * @see #getMapDisplay()
     * @generated
     */
    void setMapDisplay(IMapDisplay value);

    /**
     * Forces the area in all layers to be re-rendered. If bounds is null then the entire layer must
     * be rendered.
     * 
     * @model
     */
    public void refresh(Envelope bounds);

    /**
     * Re-composes the image from the associated contexts for the screen area and draws it on the screen.  It does not
     * re-render any data.  If you want layer to be re-renderer see <code>refresh(Envelope)</code>. 
     */
    public void refreshImage();

    /**
     * Returns the RendererCreator used to create renderers.
     * 
     * @return the RendererCreator used to create renderers.
     */
    public RendererCreator getRendererCreator();

    /**
     * Returns the value of the '<em><b>Viewport Model Internal</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.locationtech.udig.project.internal.render.ViewportModel#getRenderManagerInternal <em>Render Manager Internal</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Viewport Model</em>' reference isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Viewport Model Internal</em>' reference.
     * @see #setViewportModelInternal(ViewportModel)
     * @see org.locationtech.udig.project.internal.render.RenderPackage#getRenderManager_ViewportModelInternal()
     * @see org.locationtech.udig.project.internal.render.ViewportModel#getRenderManagerInternal
     * @model opposite="renderManagerInternal" resolveProxies="false" transient="true"
     * @generated
     */
    ViewportModel getViewportModelInternal();

    /**
     * Sets the value of the '{@link org.locationtech.udig.project.internal.render.RenderManager#getViewportModelInternal <em>Viewport Model Internal</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @param value the new value of the '<em>Viewport Model Internal</em>' reference.
     * @see #getViewportModelInternal()
     * @generated
     */
    void setViewportModelInternal(ViewportModel value);

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
