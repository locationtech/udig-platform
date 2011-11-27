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
package net.refractions.udig.boundary;

import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.part.IPageBookViewPage;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Allows lazy loading of IBoundaryStrategy (with extra information from the extension
 * point such as name and title.
 * <p>
 * Note this is a true proxy implementing IBoundaryStrategy; it does provide additional information
 * in the form of {@link #getId()} which may be used as an identifier when refering to a boundary strategy.
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
    private IBoundaryStrategy strategy = null;

    /** Identifier provided by the configuration element id attribute */
    private String id;

    /**
     * Creates a new instance of the 
     * 
     * @param config The configuration of the boundary strategy
     */
    public BoundaryProxy(IConfigurationElement config) {
        configElement = config;
        id = configElement.getAttribute("id");
    }
    
    @Override
    public ReferencedEnvelope getExtent() {
        return getStrategy().getExtent();
    }

    @Override
    public Geometry getGeometry() {
        return getStrategy().getGeometry();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return getStrategy().getCrs();
    }

    @Override
    public String getName() {
        return configElement.getAttribute("name");
    }
    
    /**
     * Gets the boundary strategy and creates it if it doesn't exist
     * @return IBoundaryStrategy
     */
    public synchronized IBoundaryStrategy getStrategy(){
        if (strategy == null) {
            try {
                strategy = (IBoundaryStrategy)configElement.createExecutableExtension("class");
            } catch (CoreException e) {
                String name = configElement.getAttribute("class");
                blame( "Strategy "+name+" not available ("+id+")", e );
            }
        }
        return strategy;
    }
    
    @Override
    public synchronized IPageBookViewPage createPage() {
        IPageBookViewPage page = null;
        try {
            // check if the page has supplied some crazy dynamic page thing
            page = getStrategy().createPage();
        } catch (Throwable t) {
            blame("Page " + id + " not available", t);
        }
        if (page == null) {
            String name = configElement.getAttribute("page");
            if (name != null && !name.isEmpty()) {
                try {
                    page = (IPageBookViewPage) configElement.createExecutableExtension("page");
                } catch (CoreException e) {
                    blame("Page " + name + " not available (" + id + ")", e);
                }
            }
        }
        return page;
    }
    
    /**
     * Blame the contributor for the following problem
     * 
     * @param message Example "bad dog"
     * @param t Throwable causing the problem (optional and may be null)
     */
    public void blame( String message, Throwable t ){
        String contributorId = configElement.getContributor().getName();
        String msg = message == null ? t.getMessage() : message + t.getMessage();        
        IStatus status = new Status(IStatus.WARNING,contributorId, msg,t);
        
        UiPlugin.getDefault().getLog().log( status );        
    }
    
    /**
     * Gets the Id of the strategy
     * @return String The Id of the strategy
     */
    public String getId() {
        return id;
    }

    public void addListener( BoundaryListener listener ) {
        getStrategy().addListener(listener);
    }

    public void removeListener( BoundaryListener listener ) {
        getStrategy().removeListener(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    protected void notifyListeners(BoundaryListener.Event changed) {
        getStrategy().notifyListeners(changed);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BoundaryProxy other = (BoundaryProxy) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
