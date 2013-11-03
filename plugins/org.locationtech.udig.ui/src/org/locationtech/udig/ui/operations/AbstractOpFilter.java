/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Abstract implementation that can be extended by OpFilter implementers.  Manages listeners primarily.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractOpFilter implements OpFilter {

    protected Set<IOpFilterListener> listeners=new CopyOnWriteArraySet<IOpFilterListener>();
    
    public void addListener( IOpFilterListener listener ) {
        listeners.add(listener);
    }


    public void removeListener( IOpFilterListener listener ) {
        listeners.add(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    protected void notifyListeners(Object changed) {
        for( IOpFilterListener listener : listeners ) {
            listener.notifyChange(changed);
        }
    }

}
