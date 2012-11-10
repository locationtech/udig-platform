/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit;

/**
 * Enumerates the type of Events that a {@link net.refractions.udig.tools.edit.EventBehaviour} can react to.
 * 
 * @author jones
 * @since 1.1.0
 */
public enum EventType {
    DOUBLE_CLICK, DRAGGED, ENTERED, EXITED, MOVED, PRESSED, RELEASED, WHEEL, HOVERED;
}
