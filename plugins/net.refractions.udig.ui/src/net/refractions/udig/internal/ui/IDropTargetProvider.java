/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.internal.ui;

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
