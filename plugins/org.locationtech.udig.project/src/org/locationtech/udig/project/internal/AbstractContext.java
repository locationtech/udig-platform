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

import org.locationtech.udig.project.IAbstractContext;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

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
     * Sets the value of the '{@link org.locationtech.udig.project.internal.AbstractContext#getRenderManagerInternal <em>Render Manager Internal</em>}' reference.
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
     * Sets the value of the '{@link org.locationtech.udig.project.internal.AbstractContext#getMapInternal <em>Map Internal</em>}' reference.
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
