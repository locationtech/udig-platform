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
package net.refractions.udig.ui.operations;

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
