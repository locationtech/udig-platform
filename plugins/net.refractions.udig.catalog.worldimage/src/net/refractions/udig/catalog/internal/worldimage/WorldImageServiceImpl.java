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
package net.refractions.udig.catalog.internal.worldimage;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.coverage.grid.GridFormatFactorySpi;

/**
 * Provides a handle to a world image service allowing the service to be lazily
 * loaded.
 * @author mleslie
 * @since 0.6.0
 */
public class WorldImageServiceImpl extends AbstractRasterService {
    private WorldImageServiceInfo info;
    private WorldImageGeoResourceImpl resource;

    /**
     * Construct <code>WorldImageServiceImpl</code>.
     *
     * @param id
     * @param factory
     */
    public WorldImageServiceImpl(URL id, GridFormatFactorySpi factory) {
        super(id, factory);
    }

    /** Added to prevent creation of new GeoResource on each call to members */
    public synchronized WorldImageGeoResourceImpl getGeoResource(IProgressMonitor monitor){
        if( resource == null ){
        	URL prjURL = null;
            java.io.File baseFile = URLUtils.urlToFile(getIdentifier());

            java.io.File[] found = URLUtils.findRelatedFiles(baseFile, ".prj"); //$NON-NLS-1$
            if( found.length>0 ){
            try {
				prjURL = found[0].toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
            }

            resource = new WorldImageGeoResourceImpl( this, getTitle(), prjURL);
        }
        return resource;
    }
    @Override
    public List<AbstractRasterGeoResource> resources( IProgressMonitor monitor )
            throws IOException {
        List<AbstractRasterGeoResource> list =
            new ArrayList<AbstractRasterGeoResource>();
        list.add( getGeoResource(monitor) );
        return list;
    }

    public IServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
        if(this.info == null) {
            this.info = new WorldImageServiceInfo();
        }
        return this.info;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return new WorldImageServiceExtension().createParams(getIdentifier());
    }
    public void dispose( IProgressMonitor monitor ) {
        // do nothing
    }

    private class WorldImageServiceInfo extends IServiceInfo {
        WorldImageServiceInfo() {
            super();
            this.keywords = new String[] {
                    "WorldImage", "world image", ".gif", ".jpg", ".jpeg",   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
                    ".tif", ".tiff", ".png"};   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }

        public String getTitle() {
            return getIdentifier().getFile();
        }

        public String getDescription() {
            return getIdentifier().toString();
        }
    }
}
