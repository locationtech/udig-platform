/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.tool.selection.provider;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;

/**
 * Implements the basic functionality of a MapEditorSelectionProvider.
 *  
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractMapEditorSelectionProvider implements IMapEditorSelectionProvider {
    protected StructuredSelection selection;
    protected CopyOnWriteArraySet<ISelectionChangedListener> listeners=new CopyOnWriteArraySet<ISelectionChangedListener>();


    public Set<ISelectionChangedListener> getListeners(){
        return listeners;
    }
    
    protected void notifyListeners() {
        if( Display.getCurrent()==null ){
            final ISelectionProvider p=this;
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    SelectionChangedEvent event=new SelectionChangedEvent(p, selection);
                    for( ISelectionChangedListener l : listeners ) {
                        l.selectionChanged(event);
                    }
                }
            });
        }else{
            SelectionChangedEvent event=new SelectionChangedEvent(this, selection);
            for( ISelectionChangedListener l : listeners ) {
                l.selectionChanged(event);
            }
        }
    }

    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        listeners.add(listener);
    }

    public ISelection getSelection() {
        return selection;
    }

    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        listeners.remove(listener);
    }

    public void setSelection( ISelection selection ) {
        throw new UnsupportedOperationException();
    }

}
