/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.arcsde.internal.Messages;
import net.refractions.udig.ui.graphics.Glyph;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

class ArcGeoResourceInfo extends IGeoResourceInfo {

    /** ArcGeoResourceInfo resource field */
    private final ArcGeoResource resource;

    private SimpleFeatureType ft = null;

    ArcGeoResourceInfo(ArcGeoResource arcGeoResource, DataStore dataStore) throws IOException {
        resource = arcGeoResource;
        ft = resource.service(new NullProgressMonitor()).getDS(null).getSchema(resource.typename);

        try {
            FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(resource.typename);
            bounds = new ReferencedEnvelope(source.getBounds(), getCRS());
            if (bounds == null) {
                bounds = new ReferencedEnvelope(new Envelope(), source.getSchema()
                        .getCoordinateReferenceSystem());
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
            CatalogPlugin
                    .getDefault()
                    .getLog()
                    .log(
                            new org.eclipse.core.runtime.Status(
                                    IStatus.WARNING,
                                    "net.refractions.udig.catalog", 0, Messages.ArcGeoResource_error_layer_bounds, e)); //$NON-NLS-1$
            bounds = new ReferencedEnvelope(new Envelope(), null);
        }

        icon = Glyph.icon(ft);
        keywords = new String[] { "postgis", //$NON-NLS-1$
                ft.getName().getLocalPart(), ft.getName().getNamespaceURI() };
    }

    public CoordinateReferenceSystem getCRS() {
        return ft.getCoordinateReferenceSystem();
    }

    public String getName() {
        return ft.getName().getLocalPart();
    }

    public URI getSchema() {
        try {
            return new URI(ft.getName().getNamespaceURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String getTitle() {
        return ft.getName().getLocalPart();
    }
}