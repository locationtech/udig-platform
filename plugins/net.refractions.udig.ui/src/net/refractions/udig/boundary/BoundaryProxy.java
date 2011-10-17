package net.refractions.udig.boundary;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Listener;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Allows lazy loading of IBoundaryStrategy (with extra information from the extension
 * point such as name and title.
 * 
 * @author Paul
 * @since 1.3.0
 */
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
    public Geometry getGeometry() {
        return getProxy().getGeometry();
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

    public void addListener( Listener listener ) {
        getProxy().addListener(listener);
    }

    public void removeListener( Listener listener ) {
        getProxy().removeListener(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    protected void notifyListeners(Object changed) {
        getProxy().notifyListeners(changed);
    }
}
