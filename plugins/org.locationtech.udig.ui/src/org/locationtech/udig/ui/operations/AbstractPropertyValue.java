/**
 * uDig - User Friendly Desktop Internet GIS client
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

import org.locationtech.udig.internal.ui.UiPlugin;

/**
 * Abstract class that can be used as a superclass for PropertyValue implementations.
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractPropertyValue<T> implements PropertyValue<T> {
    protected Set<IOpFilterListener> listeners = new CopyOnWriteArraySet<>();

    @Override
    public void addListener(IOpFilterListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IOpFilterListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies listener that the value of the filter has changed.
     */
    public void notifyListeners(Object changed) {
        for (IOpFilterListener listener : listeners) {
            try {
                listener.notifyChange(changed);
            } catch (Exception e) {
                UiPlugin.trace(UiPlugin.ID, listener.getClass(), e.getMessage(), e);
            }
        }
    }

}
