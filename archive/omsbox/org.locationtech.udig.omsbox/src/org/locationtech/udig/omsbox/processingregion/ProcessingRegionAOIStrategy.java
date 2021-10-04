/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import org.locationtech.udig.aoi.IAOIStrategy;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import org.locationtech.udig.omsbox.OmsBoxPlugin;

/**
 * Strategy to provide an AOI (Area of Interest) from the processing region
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class ProcessingRegionAOIStrategy extends IAOIStrategy {

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
