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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.locationtech.udig.project.internal.Layer;
/**
 * Sorts the layers for display in a view.  So the first layer drawn is at the bottom.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ViewerLayerSorter extends ViewerSorter {
    /**
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public int compare( Viewer viewer, Object e1, Object e2 ) {
        return ((Layer) e2).compareTo((Layer) e1);
    }

}
