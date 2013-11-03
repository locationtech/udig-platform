/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.locationtech.udig.internal.ui.IDropTargetProvider;

import org.eclipse.swt.dnd.DropTargetListener;

/**
 * The interface for uDig drop target listeners.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface UDIGDropTargetListener extends DropTargetListener {

    IDropTargetProvider getDropTargetProvider();
    void setDropTargetProvider( IDropTargetProvider newProvider );
}
