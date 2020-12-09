/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select.internal;

import java.io.IOException;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.navigation.SetViewportBBoxCommand;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.project.ui.tool.AbstractActionTool;
import org.locationtech.udig.tool.select.SelectPlugin;
import org.locationtech.udig.ui.ProgressManager;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Sets the ViewportModel bounds to equal the bounds of the selected features
 * in the selected layer.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ZoomSelection extends AbstractActionTool {

    public static final String ID = "org.locationtech.udig.tool.default.show.selection"; //$NON-NLS-1$

    public ZoomSelection() {
    }

    public void run() {
        ILayer layer = getContext().getSelectedLayer();
        if (layer.hasResource(SimpleFeatureSource.class)) {
            try {
                SimpleFeatureSource resource = featureSource(layer);
                Query query = layer.getQuery(true);
                ReferencedEnvelope bounds = resource.getBounds(query);
                if (bounds == null) {
                	FeatureCollection<SimpleFeatureType, SimpleFeature> featureResult = resource.getFeatures(query);
                	if (featureResult != null && !featureResult.isEmpty()) {
                		ReferencedEnvelope envelope = featureResult.getBounds();
	                	if (envelope != null) {
	                		bounds = new ReferencedEnvelope(envelope, layer.getCRS());
	                	}
                	}
                }
                
                if (bounds != null) {
	                // If the selection is a single point the bounds will
	                // have height == 0 and width == 0. This will break
	                // in ScaleUtils:306. Adding 1 to the extent fixes the problem:
	                if (bounds.getHeight() <= 0 || bounds.getWidth() <= 0) {
	                    bounds.expandBy(1);
	                }
	                bounds = ScaleUtils.fitToMinAndMax(bounds, layer);
	
	                getContext().sendASyncCommand(new SetViewportBBoxCommand(bounds, layer.getCRS()));
            	}
            } catch (IOException e) {
                SelectPlugin.log("failed to obtain resource", e); //$NON-NLS-1$
            }

        }
    }

    private SimpleFeatureSource featureSource( ILayer layer ) throws IOException {
        SimpleFeatureSource resource = layer.getResource(SimpleFeatureSource.class, ProgressManager.instance().get());
        return resource;
    }

    public void dispose() {
    }

}
