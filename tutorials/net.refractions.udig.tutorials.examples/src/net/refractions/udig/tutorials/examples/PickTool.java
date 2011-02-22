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
package net.refractions.udig.tutorials.examples;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.SimpleTool;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.util.NullProgressListener;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * This example is a tool that does a "Pick" from a click on the ViewportPane.  It retrieves all the features
 * intersecting the pick from the currently selected layer.
 *
 * An example of the xml in the plugin.xml might be:
 *
 * <pre>
 *    &lt extension
 *          point="net.refractions.udig.project.ui.tool" &gt
 *       &lt modalTool
 *             class="net.refractions.udig.code.examples.PickTool"
 *             id="net.refractions.udig.code.examples.pick"
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
            features.accepts( new FeatureVisitor(){
                public void visit( Feature feature ) {
                    System.out.println( feature.getID() );
                }
            }, new NullProgressListener() );
        }catch( Throwable t ){
            // do something smart, notify user probably.
        }

    }
}
