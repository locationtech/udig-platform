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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.locationtech.udig.project.ui.tool.IToolContext;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A "Page" that will have a ToolContext object set each time an the active MapEditor changes. If
 * the current editor is not a map editor the the context will be null otherwise it will be a
 * context that will operate against the map contained by the map editor.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public interface IUDIGDialogPage {
    /**
     * Sets the current context object
     */
    void setContext( IToolContext context );

    /**
     * Returns the current context object
     * 
     * @return the current context object
     */
    IToolContext getContext();

    /**
     * The method must create a control. The Layout data need not be set. The edit feature will be
     * set before this method is called.
     */
    public void createControl( Composite parent );

    /**
     * This method must return the control created by createControl.
     */
    public Control getControl();

    /**
     * Returns the desired Size of the dialog.
     */
    public Point getPreferredSize();

    /**
     * Called before createControl is called.
     * 
     * @param feature
     */
    void setFeature( SimpleFeature feature );
}
