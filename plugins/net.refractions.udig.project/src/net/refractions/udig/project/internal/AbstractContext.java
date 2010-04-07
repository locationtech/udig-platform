/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;

/**
 * Superclass for all context objects.  Has basic functionality.  Also provides methods to return type safe
 * access to the internal model.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public interface AbstractContext extends IAbstractContext {

    /**
     * The Viewport model of the toolkit's map
     * 
     * @return The viewport model.
     */
    ViewportModel getViewportModelInternal();

    /**
     * The LayerManager of the toolkit's map
     * 
     * @return The LayerManager.
     */
    EditManager getEditManagerInternal();

    /**
     * The Viewport model of the toolkit's map
     */
    public RenderManager getRenderManagerInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.AbstractContext#getRenderManagerInternal <em>Render Manager Internal</em>}' reference.
     * 
     * @param value the new value of the '<em>Render Manager Internal</em>' reference.
     */
    void setRenderManagerInternal( RenderManager value );

    /**
     * The display area of the Map.
     * 
     * @return The display area of the Map.
     */
    public IMapDisplay getMapDisplay();

    /**
     * The Map object.
     * 
     * @return The Map object
     */
    Map getMapInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.AbstractContext#getMapInternal <em>Map Internal</em>}' reference.
     * 
     * @param value the new value of the '<em>Map Internal</em>' reference.
     * @see #getMapInternal()
     */
    void setMapInternal( Map value );

    /**
     * The map's containing project
     * 
     * @return The map's containing project
     */
    public Project getProjectInternal();
    
    
}