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
package org.locationtech.udig.project.ui.operations;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;
import org.locationtech.udig.ui.operations.PropertyValue;
import org.opengis.filter.Filter;

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
        Boolean hasSelection = object.getFilter() != Filter.INCLUDE;
        return hasSelection.toString().equalsIgnoreCase(value);
    }

}
