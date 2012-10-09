/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.ui.internal;

import net.refractions.udig.internal.ui.UDIGDropHandler;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;

/**
 * interface for map editor related map parts
 * 
 * @author GDavis
 * @since 1.1.0
 */
public interface MapEditorPart extends MapPart, IEditorPart {

    public abstract MapEditorSite getMapEditorSite();
    
    // helper methods for tools
    public boolean isTesting();
    public void setTesting( boolean isTesting );

    public UDIGDropHandler getDropHandler();
    public boolean isDragging();
    public void setDragging( boolean isDragging);

    public Composite getComposite();

    public void setDirty( boolean b );
}
