/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
