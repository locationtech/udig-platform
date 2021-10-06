/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.geotools.brewer.color.BrewerPalette;

final class BrewerPaletteViewerSorter extends ViewerComparator {
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof BrewerPalette && e2 instanceof BrewerPalette) {
            BrewerPalette p1 = (BrewerPalette) e1;
            BrewerPalette p2 = (BrewerPalette) e2;
            // alphabetical by name
            return p1.getName().compareTo(p2.getName());
            // TODO: alternatives (color hue?)
        } else
            return super.compare(viewer, e1, e2);
    }
}
