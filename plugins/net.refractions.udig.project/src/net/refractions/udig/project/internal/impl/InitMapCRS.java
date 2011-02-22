/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.impl;

import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.preferences.PreferenceConstants;

/**
 * This class sets the initial CRS of a map.
 *
 * @author jesse
 */
public class InitMapCRS implements LayerInterceptor {

	public void run(Layer layer) {
	    synchronized (InitMapCRS.class) {
	        if(layer.getMap()==null ){
	            // this check is here because we could be doing a copy
	            return;
	        }
	        int defaultCRS = ProjectPlugin.getPlugin().getPreferenceStore().getInt(PreferenceConstants.P_DEFAULT_CRS);

	        // If the default CRS == -1 then the preferences declare that the CRS should be the CRS of the first layer added
	        // If the Current CRS==ViewportModel.BAD_DEFAULT then it can also be changed.  It means that the DEFAULT_CRS was
	        // an illegal EPSG code
	        CoordinateReferenceSystem crs = layer.getMap().getViewportModel().getCRS();
			if( defaultCRS!=-1 && crs==ViewportModel.BAD_DEFAULT ){
	            return;
	        }
	        List<ILayer> layers = layer.getMapInternal().getMapLayers();
	        //  If first layer or if the crs has been unchanged from the original CRS
	        if( layers.size()>1&&crs!=ViewportModel.DEFAULT_CRS){
	            return;
	        }

	        layer.getMapInternal().getViewportModelInternal().setCRS(layer.getCRS());
        }
	}

}
