/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.tool.IMapEditorSelectionProvider;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * Uses the EditManager and SelectionModel to provide a current selection.
 * <p>
 * Listen to EditManager and SelectionModel events and notify the Eclipse Platform of selection
 * changes.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Listen for EditLayer and EditFeature changes in LayerManager</li>
 * <li>Listen for SelectionModel selection changes</li>
 * <li>set the selection of the eclipse Platform to one that reflects the state of SelectionModel
 * and LayerManager </li>
 * </ul>
 * 
 * @author Jesse
 * @since 0.6
 */
public class MapEditorSelectionProvider extends AdapterImpl implements IMapEditorSelectionProvider {

    /**
     * Construct <code>MapEditorSelectionProvider</code>.
     */
    public MapEditorSelectionProvider() {
    }

    IStructuredSelection selection;
    CopyOnWriteArraySet<ISelectionChangedListener> list = new CopyOnWriteArraySet<ISelectionChangedListener>();

    protected void fireSelectionChangedEvent( final SelectionChangedEvent event ) {
        Object source = event.getSource();
        if (source == this)
            return;
        for( final ISelectionChangedListener l : list ) {
            PlatformGIS.run(new SafeRunnable(){
                public void run() {
                    l.selectionChanged(event);
                }
            });
        }
    }

    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void notifyChanged( Notification msg ) {
        fireSelectionChangedEvent(new SelectionChangedEvent(this, selection));
    }


    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        list.add(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return selection;
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        list.remove(listener);
    }

    /**
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection( ISelection selection ) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
            fireSelectionChangedEvent(new SelectionChangedEvent(this, selection));
        }
    }

    public void setActiveMap( IMap map2, final MapPart editor ) {
        this.selection = new StructuredSelection(map2);
    }

    public Set<ISelectionChangedListener> getListeners() {
        return list;
    }

}
