/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.ResourceInfo;
import org.geotools.data.wfs.WFSDataStore;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.ui.graphics.Glyph;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

class WFSGeoResourceInfo extends IGeoResourceInfo {

    private final WFSGeoResourceImpl wfsResource;

    CoordinateReferenceSystem crs = null;

    private boolean writable = false;

    WFSGeoResourceInfo(WFSGeoResourceImpl wfsGeoResourceImpl) throws IOException {
        wfsResource = wfsGeoResourceImpl;
        WFSDataStore ds = wfsResource.parent.getDS(null);

        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
                .getFeatureSource(wfsResource.typename);

        writable = featureSource instanceof FeatureStore;

        ResourceInfo resourceInfo = featureSource.getInfo();
        SimpleFeatureType ft = null;
        try {
            ft = ds.getSchema(wfsResource.typename);
        } catch (Exception crippled) {
            // unable to handle the describe feature type response for this typeName
            if (WfsPlugin.getDefault().isDebugging()) {
                crippled.printStackTrace();
            }
        }
        bounds = resourceInfo.getBounds();

        description = resourceInfo.getDescription();
        title = resourceInfo.getTitle();

        crs = resourceInfo.getCRS();
        if (crs == null && ft != null) {
            crs = ft.getCoordinateReferenceSystem();
        }

        name = wfsResource.typename;
        schema = resourceInfo.getSchema();
        if (schema == null) {
            try {
                if (ft != null) {
                    schema = new URI(ft.getName().getNamespaceURI());
                } else {
                    schema = wfsResource.parent.getID().toURI();
                }
            } catch (URISyntaxException e) {
                schema = null;
            }
        }
        Set<String> tags = new TreeSet<>();
        try {
            tags.addAll(resourceInfo.getKeywords());
        } catch (Throwable t) {
            WfsPlugin.trace("Could not retrieve keywords", t); //$NON-NLS-1$
            // no keywords for you
        }
        tags.addAll(Arrays.asList(new String[] { "wfs", wfsResource.typename })); //$NON-NLS-1$
        keywords = tags.toArray(new String[0]);
        icon = Glyph.icon(ft);
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResourceInfo#getCRS()
     */
    @Override
    public CoordinateReferenceSystem getCRS() {
        if (crs != null)
            return crs;
        return super.getCRS();
    }

    public boolean isWritable() {
        return writable;
    }
}
