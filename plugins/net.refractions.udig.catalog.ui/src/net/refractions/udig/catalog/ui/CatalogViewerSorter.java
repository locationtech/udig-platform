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
package net.refractions.udig.catalog.ui;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;

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
