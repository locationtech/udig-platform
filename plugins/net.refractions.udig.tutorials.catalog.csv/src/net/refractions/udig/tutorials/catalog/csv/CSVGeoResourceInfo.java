/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tutorials.catalog.csv;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Point;

public class CSVGeoResourceInfo extends IGeoResourceInfo {
    CSVGeoResource handle;
    public CSVGeoResourceInfo( CSVGeoResource resource, IProgressMonitor monitor ) throws IOException {
        this.handle = resource;
        this.title = handle.getIdentifier().toString();
        CSV csv = handle.getCSV( monitor );
        CsvReader reader = csv.reader();
        try {
            reader.readHeaders();
            this.description = "Information:";
            for( String header : reader.getHeaders() ){
                this.description += " "+header;
            }
            this.bounds = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
            while( reader.readRecord() ){
                Point point = CSV.getPoint( reader );
                if( point == null ) continue;
                this.bounds.expandToInclude(point.getCoordinate());                
            }
        }
        finally {
            reader.close();
        }
    }
}