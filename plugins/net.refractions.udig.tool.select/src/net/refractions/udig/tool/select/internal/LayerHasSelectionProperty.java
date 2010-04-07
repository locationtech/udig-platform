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
package net.refractions.udig.tool.select.internal;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.ui.operations.AbstractPropertyValue;

import org.opengis.filter.Filter;

/**
 * Returns true if the layer has a selection.  (layer's filter!=Filter.all)
 * @author Jesse
 * @since 1.1.0
 */
public class LayerHasSelectionProperty extends AbstractPropertyValue<ILayer>{

    private ILayerListener listener=new ILayerListener(){

        public void refresh( LayerEvent event ) {
            if( event.getType()==LayerEvent.EventType.FILTER ){
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

    public boolean isTrue( ILayer layer, String value ) {
        layer.addListener(listener);
        return layer.getFilter()!=Filter.EXCLUDE;
    }


}
