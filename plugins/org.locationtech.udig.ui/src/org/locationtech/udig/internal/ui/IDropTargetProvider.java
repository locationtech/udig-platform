/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * Implementations provide the target object for the Drag and drop code.
 * <p>A provider is used by the drop adapters to determine what the target object is</p>
 * 
 * @author jones
 * @since 1.1.0
 */
public interface IDropTargetProvider {
    /**
     * Returns the object that the dragged object will be dropped on.
     * @param event TODO
     * @param event the drag and drop event.
     */
    Object getTarget(DropTargetEvent event);
}
