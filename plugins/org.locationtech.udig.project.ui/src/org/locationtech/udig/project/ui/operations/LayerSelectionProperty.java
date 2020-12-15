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
import org.locationtech.udig.project.ILayerListener;
import org.locationtech.udig.project.LayerEvent;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;
import org.locationtech.udig.ui.operations.PropertyValue;
import org.opengis.filter.Filter;

/**
 * Checks if a layer has a selection. This property allows to enable operations if layer
 * hasSelection. If given value is set to false, its possible to check, if the layer has no
 * selection. This allows uses to provide functionality to be enabled if and only if a layer has no
 * selection filter set.
 * 
 * @author Jesse
 * @author Frank Gasdorf
 */
public class LayerSelectionProperty extends AbstractPropertyValue<ILayer>
        implements PropertyValue<ILayer> {

    private final ILayerListener layerListener = new ILayerListener() {

        public void refresh(LayerEvent event) {
            if (event.getType() == LayerEvent.EventType.FILTER) {
                notifyListeners(event.getSource());
            }
        }

    };

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isTrue(ILayer layer, String expectedBooleanAsString) {
        Boolean hasSelection = layer.getFilter() != Filter.EXCLUDE;

        layer.addListener(layerListener);

        return hasSelection.equals(expectedBooleanAsString == null ? Boolean.TRUE
                : Boolean.valueOf(expectedBooleanAsString));
    }

}
