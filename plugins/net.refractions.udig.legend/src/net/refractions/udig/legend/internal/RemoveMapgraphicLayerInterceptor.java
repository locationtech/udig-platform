/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
