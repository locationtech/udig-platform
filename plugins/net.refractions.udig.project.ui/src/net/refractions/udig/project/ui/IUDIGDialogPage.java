/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui;

import net.refractions.udig.project.ui.tool.IToolContext;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
