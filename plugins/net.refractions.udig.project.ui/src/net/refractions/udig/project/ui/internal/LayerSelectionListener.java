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
package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.internal.Layer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author jones
 * @since 0.6.0
 */
public class LayerSelectionListener implements ISelectionListener {
    private Callback callback;
    public static interface Callback {
        public void callback( List<Layer> layers );
    }

    /**
     * Construct <code>LayerSelectionListener</code>.
     * 
     * @param callback The callback used when a layer or layers have been added.
     */
    public LayerSelectionListener( Callback callback ) {
        this.callback = callback;
    }
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        if (selection instanceof IStructuredSelection) {
            List<Layer> layers = new ArrayList<Layer>();
            for( Iterator iter = ((IStructuredSelection) selection).iterator(); iter.hasNext(); ) {
                Object obj = iter.next();
                if (obj instanceof Layer)
                    layers.add((Layer) obj);
            }
            if (!layers.isEmpty())
                callback.callback(layers);
        }
    }

}
