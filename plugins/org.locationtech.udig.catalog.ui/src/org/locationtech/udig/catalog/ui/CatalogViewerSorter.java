/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;

import org.eclipse.jface.viewers.ViewerSorter;
/**
 * Sorts the Catalog Viewers so that like Services are listed together.
 * 
 * @author jones
 * @since 1.0.0
 */
public class CatalogViewerSorter extends ViewerSorter {

    Map<Class, Integer> categories = new HashMap<Class, Integer>();
    int index = 0;
    @Override
    public int category( Object element ) {
        if (element instanceof IGeoResource)
            return Integer.MAX_VALUE;

        if (categories.containsKey(element.getClass())) {
            return categories.get(element.getClass());
        }
        index++;
        categories.put(element.getClass(), index);
        return index;

    }

}
