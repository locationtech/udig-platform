/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.SimpleTool;
import org.locationtech.udig.ui.ProgressManager;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * This example is a tool that does a "Pick" from a click on the ViewportPane.  It retrieves all the features
 * intersecting the pick from the currently selected layer.
 * 
 * An example of the xml in the plugin.xml might be:
 * 
 * <pre>
 *    &lt extension
 *          point="org.locationtech.udig.project.ui.tool" &gt
 *       &lt modalTool
 *             class="org.locationtech.udig.code.examples.PickTool"
 *             id="org.locationtech.udig.code.examples.pick"
 *             name="Pick Features"
 *             onToolbar="true"
 *            tooltip="Pick Features" /&gt
 *    &lt/ extension>
 * 
 * </pre>
 * @author Jesse
 * @since 1.1.0
 */
public class PickTool extends SimpleTool{

    @Override
    protected void onMouseReleased( MapMouseEvent e ) {
        try{
            
            // convert the mouse click into map coordinates.  This is the 
            // CRS obtained from the ViewportModel
            Coordinate world = getContext().pixelToWorld(e.x, e.y);
            
            // now we must transform the coordinate to the CRS of the layer
            ILayer layer = getContext().getSelectedLayer();
            MathTransform tranform = layer.mapToLayerTransform();
            
            double[] from=new double[]{world.x, world.y};
            double[] to = new double[2];
            tranform.transform(from, 0, to, 0, 1);
            
            // Construct a envelope from the transformed coordinate
            Envelope env = new Envelope(new Coordinate(to[0],to[1])); 

            // Query the feature source to get the features that intersect with that coordinate
            FeatureSource source = layer.getResource(FeatureSource.class, ProgressManager.instance().get());
            FeatureCollection features = source.getFeatures(layer.createBBoxFilter(env, ProgressManager.instance().get()));
            
            // do something with the features...
            
        }catch( Throwable t ){
            // do something smart, notify user probably.
        }
        
    }
}
