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
package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.AbstractPropertyValue;
import net.refractions.udig.ui.operations.PropertyValue;

import org.geotools.filter.Filter;

/**
 * Checks if a layer has a selection
 * 
 * @author Jesse
 */
public class LayerSelectionProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue(ILayer object, String value) {
        Boolean hasSelection = object.getFilter() != Filter.ALL;
        return hasSelection.toString().equalsIgnoreCase(value);
    }

}
