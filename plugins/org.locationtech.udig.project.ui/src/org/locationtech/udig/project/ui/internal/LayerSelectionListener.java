/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.locationtech.udig.project.internal.Layer;

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
