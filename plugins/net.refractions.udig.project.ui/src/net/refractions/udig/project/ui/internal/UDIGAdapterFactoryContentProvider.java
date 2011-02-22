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
package net.refractions.udig.project.ui.internal;

import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.PlatformUI;

/**
 * This AdapterFactory guarantees that it will not update its listeners if the workbench has been
 * disposed.
 *
 * @author jgarnett
 * @since 1.0.0
 */
public class UDIGAdapterFactoryContentProvider extends AdapterFactoryContentProvider {
    /**
     * An interface for other objects to register as listeners for when the input has changed.
     *
     * @author Jesse
     * @since 1.1.0
     */
    public interface InputChangedListener {
        void changed();
    }

    CopyOnWriteArraySet<InputChangedListener> listeners = new CopyOnWriteArraySet<InputChangedListener>();
    /**
     * Add a listener.  A listener is only added once.
     *
     * @param newListener listener to add.
     */
    public void addListener( InputChangedListener newListener){
        listeners.add(newListener);
    }
    /**
     * Remove a listener.
     *
     * @param oldListener listener to remove
     */
    public void removeListener( InputChangedListener oldListener){
        listeners.remove(oldListener);
    }

    public UDIGAdapterFactoryContentProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    @Override
    public Object[] getChildren( Object arg0 ) {
        return super.getChildren(arg0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyChanged( Notification notification ) {
        if (PlatformUI.getWorkbench().isClosing())
            return;

        super.notifyChanged(notification);
        for( InputChangedListener l : listeners ) {
           l.changed();
        }
    }


}
