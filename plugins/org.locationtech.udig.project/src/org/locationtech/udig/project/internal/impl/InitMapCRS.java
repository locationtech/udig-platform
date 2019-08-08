/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.interceptor.LayerInterceptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.preferences.PreferenceConstants;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

	        if (isSetValidDefaultCRS(layer.getMap().getViewportModel().getCRS(), ViewportModel.BAD_DEFAULT, defaultCRS)) {
	            return;
	        }
	        List<ILayer> layers = layer.getMapInternal().getMapLayers();
	        //  If first layer or if the crs has been unchanged from the original CRS
	        if (layers.size() > 1 && layer.getMapInternal().getViewportModelInternal().isSetCRS()) {
	            return;
	        }

	        layer.getMapInternal().getViewportModelInternal().setCRS(layer.getCRS());
	    }
	}

	/**
	 * If the default CRS == -1 then the preferences declare that the CRS should be the CRS of the first layer added
	 * If the Current CRS==ViewportModel.BAD_DEFAULT then it can also be changed.  It means that the DEFAULT_CRS was
	 * an illegal EPSG code

	 * @param viewportModelCRS current CRS of viewportModel
	 * @param currentDefault configured default EPSG code for viewportModel
	 * @return true if its allows to use CRS from layer
	 */
	public static boolean isSetValidDefaultCRS(final CoordinateReferenceSystem viewportModelCRS, final CoordinateReferenceSystem badCRS, int currentDefault) {
	    return currentDefault != -1 && viewportModelCRS != badCRS;
	}
}
