/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.project.UDIGAdaptableDecorator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

/**
 * A SelectionProvider Decorator that wraps a selection provider and attempts (using the Platform's
 * Adapter Factory mechanism) to adapt all non-adaptable objects to adaptable objects.
 * 
 * @author jeichar
 * @since 0.3
 */
public class UDIGAdapterSelectionProvider implements ISelectionProvider, ISelectionChangedListener {

    private ISelectionProvider provider;
    CopyOnWriteArrayList<ISelectionChangedListener> listeners = new CopyOnWriteArrayList<ISelectionChangedListener>();

    /**
     * Construct <code>UDIGAdapterSelectionProvider</code>.*
     * 
     * @param provider
     */
    public UDIGAdapterSelectionProvider( ISelectionProvider provider ) {
        this.provider = provider;
        provider.addSelectionChangedListener(this);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        listeners.add(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        ISelection selection = provider.getSelection();
        List<Object> newSelection = new ArrayList<Object>();
        if (selection instanceof IStructuredSelection) {
            for( Iterator iter = ((IStructuredSelection) selection).iterator(); iter.hasNext(); ) {
                Object object = iter.next();
                if (object instanceof IAdaptable) {
                    newSelection.add(object);
                    continue;
                }

                Object adaptable = Platform.getAdapterManager()
                        .getAdapter(object, IAdaptable.class);
                if (adaptable == null)
                    adaptable = object;
                newSelection.add(adaptable);
            }
            selection = new StructuredSelection(newSelection);
        }

        return selection;
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        listeners.add(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection( ISelection selection ) {
        List<Object> newSelection = new ArrayList<Object>();
        if (selection instanceof IStructuredSelection) {
            for( Iterator iter = ((IStructuredSelection) selection).iterator(); iter.hasNext(); ) {
                Object object = iter.next();
                if (object instanceof UDIGAdaptableDecorator) {
                    newSelection.add(((UDIGAdaptableDecorator) object).getObject());
                    continue;
                }

                newSelection.add(object);
            }
            selection = new StructuredSelection(newSelection);
        }

        provider.setSelection(selection);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        final SelectionChangedEvent newEvent = new SelectionChangedEvent(this, getSelection());
        for (final ISelectionChangedListener listener : listeners) {
        	Display.getDefault().asyncExec(new Runnable(){
        		/**
        		 * @see java.lang.Runnable#run()
        		 */
        		public void run() {
        			listener.selectionChanged(newEvent);
        		}
        	});
        }
		}
    

    /**
     * This is ONLY for testing
     * @return Returns the provider.
     */
    public ISelectionProvider testGetProvider() {
        return provider;
    }

}
