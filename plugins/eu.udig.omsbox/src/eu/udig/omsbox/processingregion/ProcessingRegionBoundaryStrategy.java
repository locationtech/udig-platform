/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package eu.udig.omsbox.processingregion;

import net.refractions.udig.boundary.IBoundaryStrategy;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.udig.omsbox.OmsBoxPlugin;

/**
 * Strategy to provide a Boundary from the processing region
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class ProcessingRegionBoundaryStrategy extends IBoundaryStrategy {

    private static String name = "Processing Region";
    
    @Override
    public ReferencedEnvelope getExtent() {
        ILayer processingRegionLayer = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
        if (processingRegionLayer != null) {
            IStyleBlackboard blackboard = processingRegionLayer.getStyleBlackboard();
            ProcessingRegionStyle style = (ProcessingRegionStyle) blackboard.get(ProcessingRegionStyleContent.ID);
            if (style == null) {
                style = ProcessingRegionStyleContent.createDefault();
            }
            ProcessingRegion processinRegion = new ProcessingRegion(style.west, style.east, style.south, style.north, style.rows,
                    style.cols);
            ReferencedEnvelope envelope = new ReferencedEnvelope(processinRegion.getEnvelope(), getCrs()) ;
            return envelope;
        }
        return null;
    }

    @Override
    public Geometry getGeometry() {
        ReferencedEnvelope extent = this.getExtent();
        if (extent != null) {
            return new GeometryFactory().toGeometry(extent);
        }
        return null;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        ILayer processingRegionMapGraphic = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
        if (processingRegionMapGraphic != null) {
            processingRegionMapGraphic.getCRS();
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

}
