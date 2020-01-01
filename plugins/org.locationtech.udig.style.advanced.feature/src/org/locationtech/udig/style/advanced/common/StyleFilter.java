/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.common;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;

/**
 * Viewer filter on style names.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class StyleFilter extends ViewerFilter {

    private String searchString;

    public void setSearchText( String s ) {
        // Search must be a substring of the existing value
        this.searchString = ".*" + s + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {
        if (searchString == null || searchString.length() == 0) {
            return true;
        }
        StyleWrapper p = (StyleWrapper) element;
        if (p.getName().matches(searchString)) {
            return true;
        }
        return false;
    }
}
