/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui.operations;


/**
 * A listener that is notified when the value of the OpFilter may have changed.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IOpFilterListener {

    /**
     * Notifies that a listener has changed.
     * @param changed the object that has changed and whose cache must be emptied.
     */
    void notifyChange(Object changed);

}
