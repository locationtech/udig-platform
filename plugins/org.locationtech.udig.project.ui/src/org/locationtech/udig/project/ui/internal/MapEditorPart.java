/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.locationtech.udig.internal.ui.UDIGDropHandler;

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
