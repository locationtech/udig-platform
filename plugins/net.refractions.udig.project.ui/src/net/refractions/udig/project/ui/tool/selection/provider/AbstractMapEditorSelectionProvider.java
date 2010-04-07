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
package net.refractions.udig.project.ui.tool.selection.provider;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.project.ui.tool.IMapEditorSelectionProvider;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

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
