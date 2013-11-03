/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.legend.internal;

import org.locationtech.udig.legend.LegendPlugin;
import org.locationtech.udig.legend.ui.LegendGraphic;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.project.interceptor.LayerInterceptor;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.impl.MapImpl;

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
