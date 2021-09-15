/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Since a MapEditor can have a different selection provider depending on the tool that is selected, this class
 * is needed to manage the changing selection providers.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ReplaceableSelectionProvider implements ISelectionProvider {

    private static final ISelection EMPTY = new ISelection(){

        public boolean isEmpty() {
            return true;
        }

    };

    private Set<ISelectionChangedListener> listeners = new CopyOnWriteArraySet<ISelectionChangedListener>();

    private ISelectionProvider wrapped;

    private ISelectionChangedListener listener = new ISelectionChangedListener(){

        public void selectionChanged(SelectionChangedEvent event) {
            if (event.getSelectionProvider() != wrapped) {
                event.getSelectionProvider().removeSelectionChangedListener(this);
            }
            notifyChange(event.getSelection());
        }

    };

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.add(listener);
    }

    public ISelection getSelection() {
        if (wrapped != null) {
            return wrapped.getSelection();
        } else {
            return EMPTY;
        }
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listeners.remove(listener);
    }

    public void setSelection(ISelection selection) {
        if (wrapped != null) {
            wrapped.setSelection(selection);
        }
    }

    public void setProvider(ISelectionProvider newProvider){
        if (wrapped != null) {

            wrapped.removeSelectionChangedListener(listener);
            newProvider.addSelectionChangedListener(listener);
            wrapped = newProvider;

            if (newProvider.getSelection() != null) {
                notifyChange(newProvider.getSelection());
            }

        }

    }

    private void notifyChange(ISelection selection) {
        SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
        for (ISelectionChangedListener l : listeners) {
            l.selectionChanged(event);
        }
    }

    public ISelectionProvider getSelectionProvider() {
        return wrapped;
    }

}
