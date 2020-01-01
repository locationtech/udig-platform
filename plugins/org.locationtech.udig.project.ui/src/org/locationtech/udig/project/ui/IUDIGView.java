/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui;

import org.eclipse.ui.IViewPart;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A View that will have a ToolContext object set each time an editor changes. If the current editor
 * is not a map editor the the context will be null otherwise it will be a context that will operate
 * against the map contained by the map editor.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public interface IUDIGView extends IViewPart {
    /**
     * Sets the current context object
     */
    void setContext( IToolContext newContext );

    /**
     * Returns the current context object
     * 
     * @return the current context object
     */
    IToolContext getContext();

    /**
     * Called when the the current edit feature changes.
     * 
     * @param feature
     */
    void editFeatureChanged( SimpleFeature feature );

}
