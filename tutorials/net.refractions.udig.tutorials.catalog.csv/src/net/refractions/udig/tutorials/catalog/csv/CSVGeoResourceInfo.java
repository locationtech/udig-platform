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
