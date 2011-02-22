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
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.rasterings.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.parameter.ParameterGroup;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Provides a handle to a raster resource allowing the service to be lazily
 * loaded.
 * <p>
 * This class provides functionality common to GridCoverage based resources.
 * @author mleslie
 * @since 0.6.0
 */
public abstract class AbstractRasterGeoResource extends IGeoResource {
        /**
         * <code>service</code> field recalls the service that created
         * this resource
         */
        protected AbstractRasterService service;
        private volatile SoftReference<GridCoverage> coverage;
        private ParameterGroup readParams;
        private String name;
        private Throwable msg;

        /**
         * Construct <code>AbstractRasterGeoResource</code>.
         *
         * @param service The service creating this resource.
         * @param name Human readable name of this resource.
         */
        public AbstractRasterGeoResource(AbstractRasterService service, String name) {
            this.service = service;
            if(name == null) {
                name = service.getIdentifier().getFile();
                int slash = name.lastIndexOf('/');
                name = name.substring(
                        (slash == -1 && slash < name.length() - 1
                                ? 0 : name.lastIndexOf('/')) + 1,
                        (name.lastIndexOf('.') == -1
                                ? name.length() : name . lastIndexOf('.')));

            }
            this.name = name;
        }

        public Status getStatus() {
            return this.service.getStatus();
        }

        public Throwable getMessage() {
            if( msg != null ){
                return msg;
            }
            else {
                return this.service.getMessage();
            }
        }

        /**
         * Retrieves the parameters used to create the GridCoverageReader
         * for this resource.  This simply delegates the creation of these
         * parameters to a GridFormat.
         *
         * @return ParameterGroup describing the GeoResource
         */
        public ParameterGroup getReadParameters() {
            if( this.readParams==null ){
                ParameterValueGroup desc =
                        ((Format)this.service.getFormat()).getReadParameters();
                if( desc == null )
                    return null;
                if(desc instanceof ParameterGroup) {
                    this.readParams = (ParameterGroup)desc;
                }
            }
            return this.readParams;
        }

        /**
         * Finds or creates the GridCoverage for this resource.
         *
         * @return GridCoverage for this GeoResource
         * @throws IOException
         */
        public synchronized Object findResource() throws IOException {
            if( this.coverage==null || this.coverage.get()==null ) {
                try {
                    GridCoverageReader reader = this.service.getReader(null);
                    ParameterGroup pvg = getReadParameters();
                    List list = pvg.values();
                    @SuppressWarnings("unchecked") GeneralParameterValue[] values =  //$NON-NLS-1$
                            (GeneralParameterValue[])list.toArray(new GeneralParameterValue[0]);
                    this.coverage=new SoftReference<GridCoverage>(reader.read(values));
                }
                catch( Throwable t ){
                    msg = t;
                    RasteringsPlugin.log("error reading coverage", t);
                    return null;
                }
            }
            return this.coverage.get();
        }

        public URL getIdentifier() {
            try {
                return new URL(this.service.getIdentifier().toString()+"#"+this.name); //$NON_NLS-1$ //$NON-NLS-1$
            } catch(MalformedURLException ex) {
                msg = ex;
                return this.service.getIdentifier();
            }
        }

        public <T>T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
            if(monitor != null)
                monitor.beginTask(Messages.AbstractRasterGeoResource_resolve, 3);
            if(adaptee == null) {
                if(monitor != null)
                    monitor.done();
                return null;
            }
            if(adaptee.isAssignableFrom(GridCoverage.class)) {
                if(monitor != null)
                    monitor.done();
                return adaptee.cast(findResource());
            }
            if (adaptee.isAssignableFrom(GridCoverageReader.class)) {
				return adaptee.cast(service.getReader(monitor));
			}
            if(monitor != null)
                monitor.worked(1);
            if(monitor != null)
                monitor.worked(1);
            if(adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
                if(monitor != null)
                    monitor.done();
                return adaptee.cast(getInfo(monitor));
            }
            return super.resolve(adaptee, monitor);
        }
        public IService service( IProgressMonitor monitor ) throws IOException {
            return service;
        }
        public <T> boolean canResolve(Class<T> adaptee) {
            if(adaptee == null) return false;
            return(adaptee.isAssignableFrom(IGeoResourceInfo.class) ||
                    adaptee.isAssignableFrom(IService.class) ||
                    adaptee.isAssignableFrom(GridCoverage.class)||
    				adaptee.isAssignableFrom(GridCoverageReader.class))
                    || super.canResolve(adaptee);
        }

        public abstract IGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException;

}
