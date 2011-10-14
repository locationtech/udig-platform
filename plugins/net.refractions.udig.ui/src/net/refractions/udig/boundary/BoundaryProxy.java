package net.refractions.udig.boundary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class BoundaryProxy extends IBoundaryStrategy {

    /**
     * Configuration from extension registry for this boundary strategy
     */
    private IConfigurationElement configElement = null;
    
    /**
     * Boundary strategy to be lazy loaded
     */
    private IBoundaryStrategy proxy = null;
    
    /**
     * Creates a new instance of the 
     * 
     * @param config The configuration of the boundary strategy
     */
    public BoundaryProxy(IConfigurationElement config) {
        configElement = config;
    }
    
    @Override
    public ReferencedEnvelope getExtent() {
        return getProxy().getExtent();
    }

    @Override
    public Geometry getBoundary() {
        return getProxy().getBoundary();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return getProxy().getCrs();
    }

    @Override
    public String getName() {
        return configElement.getAttribute("name");
    }
    
    /**
     * Gets the boundary strategy and creates it if it doesn't exist
     * @return IBoundaryStrategy
     */
    private IBoundaryStrategy getProxy(){
        if (proxy == null) {
            try {
                proxy = (IBoundaryStrategy)configElement.createExecutableExtension("class");
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return proxy;
    }
    
    /**
     * Gets the Id of the strategy
     * @return String The Id of the strategy
     */
    public String getId() {
        return configElement.getAttribute("id");
    }

}
