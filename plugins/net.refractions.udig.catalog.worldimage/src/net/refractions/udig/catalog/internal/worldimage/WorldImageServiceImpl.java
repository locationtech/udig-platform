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
package net.refractions.udig.catalog.internal.worldimage;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;
import net.refractions.udig.catalog.rasterings.AbstractRasterServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.gce.image.WorldImageFormatFactory;

/**
 * Provides a handle to a world image service allowing the service to be lazily loaded.
 * 
 * @author mleslie
 * @since 0.6.0
 */
public class WorldImageServiceImpl extends AbstractRasterService {
    private AbstractRasterGeoResource resource;

    /**
     * Construct <code>WorldImageServiceImpl</code>.
     * 
     * @param id
     * @param factory
     */
    public WorldImageServiceImpl( URL id2, WorldImageFormatFactory factory ) {
        super(id2, WorldImageServiceExtension.TYPE, factory);
    }

    /** Added to prevent creation of new GeoResource on each call to members */
    public synchronized AbstractRasterGeoResource getGeoResource( IProgressMonitor monitor ) {
        if (resource == null) {
            URL prjURL = null;
            java.io.File baseFile = URLUtils.urlToFile(getIdentifier());

            java.io.File[] found = URLUtils.findRelatedFiles(baseFile, ".prj"); //$NON-NLS-1$
            if (found.length > 0) {
                try {
                    prjURL = found[0].toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            resource = new WorldImageGeoResourceImpl(this, getHandle(), prjURL);
        }
        return resource;
    }
    @Override
    public List<AbstractRasterGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        List<AbstractRasterGeoResource> list = new ArrayList<AbstractRasterGeoResource>();
        list.add(getGeoResource(monitor));
        return list;
    }

    protected AbstractRasterServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask("world image", 2);
        try {
            monitor.worked(1);
            return new AbstractRasterServiceInfo(this,
                    "WorldImage", "world image", ".gif", ".jpg", ".jpeg", //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
                    ".tif", ".tiff", ".png"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$);
        } finally {
            monitor.done();
        }
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(WorldImageServiceExtension.URL_PARAM, getIdentifier());
        return params;
    }

}
