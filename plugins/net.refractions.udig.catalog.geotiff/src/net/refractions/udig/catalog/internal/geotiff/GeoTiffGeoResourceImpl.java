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
package net.refractions.udig.catalog.internal.geotiff;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.geotiff.internal.Messages;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides a handle to a geotiff resource allowing the service to be lazily
 * loaded.
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffGeoResourceImpl extends AbstractRasterGeoResource {
    private GeoTiffGeoResourceInfo info;

    /**
     * Construct <code>GeoTiffGeoResourceImpl</code>.
     *
     * @param service
     * @param name
     */
    public GeoTiffGeoResourceImpl(AbstractRasterService service, String name) {
        super(service, name);
    }

    public IGeoResourceInfo getInfo(IProgressMonitor monitor) {
        if(monitor != null)  {
            monitor.beginTask(Messages.GeoTiffGeoResource_connect, 2);
            monitor.worked(1);
        }
        if(this.info == null) {
            this.info = new GeoTiffGeoResourceInfo();
            if(monitor != null)
                monitor.worked(1);
        }
        if(monitor != null)
            monitor.done();
        return this.info;
    }

    /**
     * Describes this Resource.
     * </code></pre>
     * </p>
     * @author mleslie
     * @since 0.6.0
     */
    public class GeoTiffGeoResourceInfo extends IGeoResourceInfo {
        GeoTiffGeoResourceInfo() {
            this.keywords = new String[] {
                    "GeoTiff", ".tif", ".tiff"};   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            this.title = getIdentifier().getFile();
            int indexOf = title.lastIndexOf('/');
            if( indexOf>-1 && indexOf<title.length() ){
                title=title.substring(indexOf+1);
            }
            this.description = getIdentifier().toString();
            this.bounds = getBounds();
        }

        public ReferencedEnvelope getBounds() {
            if(this.bounds == null) {
                Envelope env = null;
                try {
                    GridCoverage source = (GridCoverage)findResource();
                    if(source == null) {
                        return null;
                    }
                    org.opengis.spatialschema.geometry.Envelope ptBounds =
                        source.getEnvelope();
                    env = new Envelope( ptBounds.getMinimum(0), ptBounds.getMaximum(0),
                            ptBounds.getMinimum(1), ptBounds.getMaximum(1));

                    CoordinateReferenceSystem geomcrs =
                        source.getCoordinateReferenceSystem();
                    if(geomcrs == null) {
                        geomcrs=DefaultEngineeringCRS.GENERIC_2D;
                    }

                    this.bounds = new ReferencedEnvelope(env, geomcrs);
                } catch (Exception e) {
                    CatalogPlugin.getDefault().getLog().log(new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, "Error while getting the bounds of a layer", e ));   //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            return this.bounds;
        }
    }
}
