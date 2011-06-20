package net.refractions.udig.catalog.rasterings;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IServiceInfo;

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
        
        int numberOfexpectedInfoFields = 2;
        int numberOfAvailableInfoFields = 0;

        AbstractGridCoverage2DReader reader = null;

        try {
            IProgressMonitor monitor = new NullProgressMonitor();
            monitor.setTaskName("Checking availability of metadata for " + service.getTitle());
            
            reader = service.getReader(monitor);

            CoordinateReferenceSystem CRS = reader.getCrs();

            if (!CRS.equals(DefaultEngineeringCRS.GENERIC_2D))
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
