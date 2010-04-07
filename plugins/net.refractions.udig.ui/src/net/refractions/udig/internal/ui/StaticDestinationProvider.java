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
 * Simple Destination Provider that returns the object passed as the constructor.
 * @author jones
 * @since 1.0.0
 */
public class StaticDestinationProvider implements IDropTargetProvider {
    private Object destination;
    public StaticDestinationProvider(Object destination) {
        this.destination=destination;
    }
    public Object getTarget(DropTargetEvent event) {
        return destination;
    }

}
