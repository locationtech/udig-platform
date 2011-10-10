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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import net.refractions.udig.internal.ui.UiPlugin;

/**
 * Abstract class that can be used as a superclass for PropertyValue implementations. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractPropertyValue<T> implements PropertyValue<T> {
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
    public void notifyListeners(Object changed) {
        for( IOpFilterListener listener : listeners ) {
            try {
                listener.notifyChange(changed);
            } catch (Exception e) {
                UiPlugin.trace(UiPlugin.ID, listener.getClass(), e.getMessage(), e );
            }
        }
    }

}
