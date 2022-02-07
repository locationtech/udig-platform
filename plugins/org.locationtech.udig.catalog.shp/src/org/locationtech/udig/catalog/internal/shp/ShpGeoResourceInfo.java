/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2010, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.catalog.internal.shp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.FeatureSource;
import org.geotools.data.ResourceInfo;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.shp.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

class ShpGeoResourceInfo extends IGeoResourceInfo {
    private final ShpGeoResourceImpl shpResource;

    private SimpleFeatureType featureType = null;

    private ResourceInfo header;

    /**
     * Connect to Shapefile and grab header information.
     *
     * @param shpGeoResourceImpl
     * @throws IOException
     */
    ShpGeoResourceInfo(ShpGeoResourceImpl shpGeoResourceImpl) throws IOException {
        shpResource = shpGeoResourceImpl;
        featureType = shpResource.parent.getDS(null).getSchema();
        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> source = shpResource.parent.getDS(null)
                    .getFeatureSource();
            header = source.getInfo();
            bounds = header.getBounds();

            Envelope tmpBounds = source.getBounds();
            if (tmpBounds instanceof ReferencedEnvelope) {
                bounds = (ReferencedEnvelope) tmpBounds;
            }
            if (tmpBounds != null) {
                bounds = new ReferencedEnvelope(tmpBounds, getCRS());
            }

            if (bounds == null) {
                bounds = new ReferencedEnvelope(new Envelope(), getCRS());
                FeatureIterator<SimpleFeature> iter = source.getFeatures().features();
                try {
                    while (iter.hasNext()) {
                        SimpleFeature element = iter.next();
                        if (bounds.isNull())
                            bounds.init(element.getBounds());
                        else
                            bounds.include(element.getBounds());
                    }
                } finally {
                    iter.close();
                }
            }
        } catch (Exception e) {
            CatalogPlugin.getDefault().getLog()
                    .log(new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "org.locationtech.udig.catalog", 0, //$NON-NLS-1$
                            Messages.ShpGeoResourceImpl_error_layer_bounds, e));
            bounds = new ReferencedEnvelope(new Envelope(), getCRS());
        }

        Name ftName = featureType.getName();
        Set<String> keywordsSet = new HashSet<>();
        keywordsSet.add(".shp"); //$NON-NLS-1$
        keywordsSet.add("Shapefile"); //$NON-NLS-1$
        keywordsSet.add(ftName.getLocalPart());
        if (StringUtils.isNotEmpty(ftName.getNamespaceURI())) {
            keywordsSet.add(ftName.getNamespaceURI());
        }
        keywords = keywordsSet.toArray(new String[] {});
        title = ftName.getLocalPart();
        title = title.replace('_', ' ');
        title = title.replace("%20", " ");
        title = title.trim();
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        return featureType.getCoordinateReferenceSystem();
    }

    @Override
    public String getName() {
        return featureType.getName().getLocalPart();
    }

    @Override
    public URI getSchema() {
        Name name = featureType.getName();
        if (name.getNamespaceURI() != null) {
            try {
                return new URI(name.getNamespaceURI());
            } catch (URISyntaxException e) {
            }
        }
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Description of shapefile contents.
     *
     * @return description of Shapefile Contents
     */
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }
}
