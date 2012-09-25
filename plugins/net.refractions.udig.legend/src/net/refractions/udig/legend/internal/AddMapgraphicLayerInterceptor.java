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

import net.refractions.udig.legend.LegendPlugin;
import net.refractions.udig.legend.ui.LegendGraphic;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.impl.MapImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * A layer interceptor that addes a listener to legend
 * mapgraphic layers to refereshs the legend when the 
 * layers or styles change.
 *  
 * @author egouge
 *
 */
public class AddMapgraphicLayerInterceptor implements LayerInterceptor {

	public static final String MAPGRAPHIC_ADAPTER_KEY = "LEGEND_GRAPHIC_ADAPTER"; //$NON-NLS-1$

	@Override
	public void run(Layer layer) {
		try{
		if (layer.hasResource(MapGraphic.class)){
			MapGraphic mg = layer.getResource(MapGraphic.class, null);
			if (mg == null){
				return;
			}else if (!(mg instanceof LegendGraphic)){
				return;
			}
			
			final Layer thislayer = layer;
			
			AdapterImpl thisAdapter = new AdapterImpl() {
				public void notifyChanged(Notification event) {
					if (event.getNotifier() instanceof Layer) {
						int eventid = event.getFeatureID(Layer.class);
						if (eventid == ProjectPackage.LAYER__STYLE_BLACKBOARD ||
								eventid == ProjectPackage.LAYER__NAME ||
								eventid == ProjectPackage.LAYER__VISIBLE ){
								updateLayer();
						}
				
					} else if (event.getNotifier() instanceof ContextModel) {
						if ( event.getFeatureID(ContextModel.class)  == ProjectPackage.CONTEXT_MODEL__LAYERS){
							updateLayer();
						}
					}
				}

				private void updateLayer() {
					thislayer.refresh(null);
				}
			};
			layer.getBlackboard().put(MAPGRAPHIC_ADAPTER_KEY, thisAdapter);	//for removal later
			((MapImpl)layer.getMap()).addDeepAdapter(thisAdapter);
			
		}
		
		}catch (Exception ex){
			LegendPlugin.log("Error creating legend mapgraphic layer listener", ex); //$NON-NLS-1$
		}

	}

}
