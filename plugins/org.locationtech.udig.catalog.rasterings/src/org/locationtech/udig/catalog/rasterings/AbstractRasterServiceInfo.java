/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */

package org.locationtech.udig.catalog.rasterings;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IServiceInfo;

public class AbstractRasterServiceInfo extends IServiceInfo {

    private final AbstractRasterService service;
    
    private static double UNCACHED = -1.0;
    private double completeness = UNCACHED;

    public AbstractRasterServiceInfo( AbstractRasterService service, String... keywords ) {
        this.service = service;
        super.keywords = keywords; 
    }
    
    @Override
    public String getTitle() {
        ID id = service.getID();
        
        String title;
        if( id.isFile() ){
            title = id.toFile().getAbsolutePath();            
        }
        else {
            title = id.toString();
        }
        return title;
    }

    @Override
    public String getShortTitle() {
        return service.getID().toFile().getName();
    }
    
    @Override
    public String getDescription() {
        return service.getIdentifier().toString();
    }
    
    @Override
    public double getMetric() {
        if (!(completeness == UNCACHED)){
            return completeness;
        }
        
        final int numberOfexpectedInfoFields = 2;
        int numberOfAvailableInfoFields = 0;

        AbstractGridCoverage2DReader reader = null;

        try {
            IProgressMonitor monitor = new NullProgressMonitor();
            monitor.setTaskName("Checking availability of metadata for " + service.getTitle());
            
            reader = service.getReader(monitor);

            CoordinateReferenceSystem CRS = reader.getCoordinateReferenceSystem();

            if (!(CRS instanceof DefaultEngineeringCRS))
                numberOfAvailableInfoFields++;
            
            GeneralEnvelope env = reader.getOriginalEnvelope();
            
            if (!(env.isEmpty() || env.isNull() || env.isInfinite()))
                numberOfAvailableInfoFields++;

            completeness = (double) numberOfexpectedInfoFields
                    / (double) numberOfAvailableInfoFields;

        } finally {
            reader.dispose();//clean up render.
        }
        
        return completeness;
    }
    
}
