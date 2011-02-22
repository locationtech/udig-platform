/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.rasterings;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.coverage.grid.AbstractGridFormat;
import org.geotools.data.coverage.grid.GridFormatFactorySpi;
import org.opengis.coverage.grid.GridCoverageReader;

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
    private URL id;
    /** <code>status</code> field describes the status of the service */
    protected Status status = Status.NOTCONNECTED;
    /** <code>message</code> field reports any errors encountered. May be null. */
    protected Exception message = null;
    /** <code>reader</code> field */
    protected GridCoverageReader reader;
    private AbstractGridFormat format;
    private GridFormatFactorySpi factory;

    /**
     * Construct <code>AbstractRasterService</code>.
     *
     * @param id
     * @param factory
     */
    public AbstractRasterService( URL id, GridFormatFactorySpi factory ) {
        this.id = id;
        this.factory = factory;
    }

    /**
     * Adapts to to provide GridCoverageReader
     */
    public <T> boolean canResolve(Class<T> adaptee) {
        return adaptee != null && (
            adaptee.isAssignableFrom( GridCoverageReader.class ) ||
            super.canResolve(adaptee) );
    }
    public Status getStatus() {
        return this.status;
    }

    public Throwable getMessage() {
        return this.message;
    }

    public URL getIdentifier() {
        return this.id;
    }

    /**
     * Finds or creates the GridFormat object describing the service.
     *
     * @return GridFormat describing this coverage.
     */
    public Object getFormat() {
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
    public GridCoverageReader getReader(IProgressMonitor monitor) {
        if (this.reader == null) {
            try {
                AbstractGridFormat frmt = (AbstractGridFormat) getFormat();
                URL id = getIdentifier();
                if( id.toExternalForm().startsWith("C:/")){
                    id = new URL("file:///"+id.toExternalForm());
                }
                this.reader = frmt.getReader(getIdentifier());
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

    /**
     * Retrieves a relatively human readable title for this service.
     *
     * @return Title of this service
     */
    public String getTitle() {
        return getIdentifier().getFile();
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(GridCoverageReader.class)) {
            return adaptee.cast(getReader(monitor));
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    public abstract List<AbstractRasterGeoResource> resources( IProgressMonitor monitor )
            throws IOException;


    public abstract IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException;
}
