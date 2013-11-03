/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.rasterings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;

/**
 * Provides a handle to a raster service allowing the service to be lazily loaded.
 * <p>
 * This class provides functionality common to services attached to GridCoverage based resources.
 * </p>
 * 
 * @author mleslie
 * @since 0.6.0
 */
public abstract class AbstractRasterService extends IService {
    private ID id;
        
    /** <code>message</code> field reports any errors encountered. May be null. */
    protected Exception message = null;
    
    /** <code>reader</code> field */
    protected AbstractGridCoverage2DReader reader;
    
    private AbstractGridFormat format;
    
    private GridFormatFactorySpi factory;

    /**
     * Construct <code>AbstractRasterService</code>.
     * 
     * @param id
     * @param factory
     */
    public AbstractRasterService( URL url, String typeQualifier, GridFormatFactorySpi factory ) {
        this.id = new ID( url, typeQualifier );
        this.factory = factory;
    }

    /**
     * Adapts to to provide AbstractGridCoverage2DReader if needed.
     */
    public <T> boolean canResolve(Class<T> adaptee) {
        return adaptee != null && (
            adaptee.isAssignableFrom( AbstractGridCoverage2DReader.class ) ||
            super.canResolve(adaptee) );
    }
    public Status getStatus() {
        if( reader == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }
    public void dispose( IProgressMonitor monitor ) {
        if( reader != null ){
            reader.dispose();
            reader = null;
        }
        super.dispose(monitor);
    }
    
    public Throwable getMessage() {
        return this.message;
    }

    public URL getIdentifier() {
        return id.toURL();
    }
    
    public ID getID() {
        return id;
    }

    /**
     * Finds or creates the GridFormat object describing the service.
     * 
     * @return GridFormat describing this coverage.
     */
    public synchronized AbstractGridFormat getFormat() {
        if (this.format == null) {
            this.format = (AbstractGridFormat) this.factory.createFormat();
        }
        return this.format;
    }

    /**
     * Finds or creates the Reader used to access this service. Apon any exception, the message
     * field is populated and null is returned.
     * 
     * @return Reader linked to this service.
     */
    public synchronized AbstractGridCoverage2DReader getReader(IProgressMonitor monitor) {
        if (this.reader == null) {
            try {
                AbstractGridFormat frmt = (AbstractGridFormat) getFormat();
                ID id = getID();
                if( id.isFile() ){
                    File file = id.toFile();
                    if (file != null) {
                        // to force crs
                        // Hints hints = new Hints();
                        // hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, )
                        this.reader = (AbstractGridCoverage2DReader) frmt.getReader(file);
                        return this.reader;
	                }
	                else {
	                	throw new FileNotFoundException( id.toFile().toString() );
	                }
                }
                this.reader = (AbstractGridCoverage2DReader) frmt.getReader( id.toURL() );
            } catch (Exception ex) {
                this.message = ex;
            }
        }
        return this.reader;
    }

    /**
     * Retrieves the string identifying this service. This is the location of the resource to be
     * loaded.
     * 
     * @return String describing this service.
     */
    public String getDescription() {
        return getIdentifier().toString();
    }

    /** Retrieves a relatively human readable title for this service. */     
    public String getHandle(){
    	// we should check the Reader in order to reveal any internal content
    	// or assume "raster" to agree with Symbology encoding specification
    	// for now we will use the filename.    	
    	return getID().toBaseFile();
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(AbstractGridCoverage2DReader.class)) {
            return adaptee.cast(getReader(monitor));
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    public abstract List<AbstractRasterGeoResource> resources( IProgressMonitor monitor )
            throws IOException;


    @Override
    public AbstractRasterServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (AbstractRasterServiceInfo) super.getInfo(monitor);
    }
}
