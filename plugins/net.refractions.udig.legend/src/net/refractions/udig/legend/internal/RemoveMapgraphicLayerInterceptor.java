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
package net.refractions.udig.legend.internal;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.impl.MapImpl;

/**
 * Layer interceptor that removes the listener
 * added for the legend layer that refreshes the legend.
 * 
 * @author egouge
 *
 */
public class RemoveMapgraphicLayerInterceptor implements LayerInterceptor {

	@Override
	public void run(Layer layer) {
		
		if (layer.hasResource(MapGraphic.class)){
			AdapterImpl x = (AdapterImpl) layer.getBlackboard().get(AddMapgraphicLayerInterceptor.MAPGRAPHIC_ADAPTER_KEY);
			if (x != null){
				((MapImpl)layer.getMap()).removeDeepAdapter(x);
			}
		}
	}

}
