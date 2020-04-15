/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wms;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.xml.WMSSchema;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WMSGeoResourceInfo extends IGeoResourceInfo {
    /** WMSResourceInfo resource field */
    private final WMSGeoResourceImpl resource;

    WMSGeoResourceInfo( WMSGeoResourceImpl geoResourceImpl, IProgressMonitor monitor ) throws IOException {
        resource = geoResourceImpl;
        WebMapServer wms = resource.service(monitor).getWMS(monitor);
        WMSCapabilities caps = wms.getCapabilities();
        
        bounds = bbox( resource.layer );
        if( bounds == null ){
            bounds = new ReferencedEnvelope( CRS.getEnvelope( DefaultGeographicCRS.WGS84 ) );
        }

        String parentid = resource.service(monitor) != null && resource.service(monitor).getIdentifier() != null ? resource.getIdentifier()
                .toString() : ""; //$NON-NLS-1$
        name = resource.layer.getName();
        List<String> keywordsFromWMS = new ArrayList<String>();
        if (caps.getService().getKeywordList() != null) {
            keywordsFromWMS.addAll(Arrays.asList(caps.getService().getKeywordList()));
        }

        if (resource.layer.getKeywords() != null) {
            keywordsFromWMS.addAll(Arrays.asList(resource.layer.getKeywords()));
        }
        keywordsFromWMS.add("WMS"); //$NON-NLS-1$
        keywordsFromWMS.add(resource.layer.getName());
        keywordsFromWMS.add(caps.getService().getName());
        keywordsFromWMS.add(parentid);
        keywords = keywordsFromWMS.toArray(new String[keywordsFromWMS.size()]);

        if (resource.layer.get_abstract() != null && resource.layer.get_abstract().length() != 0) {
            description = resource.layer.get_abstract();
        } else {
            description = caps.getService().get_abstract();
        }
        description = caps.getService().get_abstract();

        if (resource.layer.getTitle() != null && resource.layer.getTitle().length() != 0) {
            title = resource.layer.getTitle();
        } else {
            title = caps.getService().getTitle();
        }
        super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(
                ISharedImages.GRID_OBJ);
    }
    /**
     * Isolate bbox generation into a single method.
     * <p>
     * This implementation uses layer.getBoundingBoxes(), we should also
     * be able to use getLatLonBoundingBox() as a backup plan.
     * 
     * @param layer
     * @return
     */
    public static ReferencedEnvelope bbox(Layer layer) {
        Envelope env;
        CoordinateReferenceSystem crs;
        
        Map<String, CRSEnvelope> boundingBoxes = layer.getBoundingBoxes();
        if (boundingBoxes.isEmpty()) {
            crs = DefaultGeographicCRS.WGS84;
            env = layer.getEnvelope(crs);
        } else {
            CRSEnvelope bbox;
            String epsg4326 = "EPSG:4326"; //$NON-NLS-1$
            String epsg4269 = "EPSG:4269"; //$NON-NLS-1$
            if (boundingBoxes.containsKey(epsg4326)) {
                bbox = (CRSEnvelope) boundingBoxes.get(epsg4326);
            } else if (boundingBoxes.containsKey(epsg4269)) {
                bbox = (CRSEnvelope) boundingBoxes.get(epsg4269);
            } else {
                bbox = (CRSEnvelope) boundingBoxes.values().iterator().next();
            }
            try {
                crs = CRS.decode(bbox.getEPSGCode());
                env = new ReferencedEnvelope(bbox.getMinX(), bbox.getMaxX(), bbox.getMinY(),
                        bbox.getMaxY(), crs);
            } catch (NoSuchAuthorityCodeException e) {
                crs = DefaultGeographicCRS.WGS84;
                env = layer.getEnvelope(crs);
            } catch (FactoryException e) {
                crs = DefaultGeographicCRS.WGS84;
                env = layer.getEnvelope(crs);
            }
        }
        return new ReferencedEnvelope( env.getMinimum(0), env.getMaximum(0), env
                .getMinimum(1), env.getMaximum(1), crs);
    }
    
    public String getName() {
        return name;
    }
    public URI getSchema() {
        return WMSSchema.NAMESPACE;
    }
    public String getTitle() {
        return title;
    }
    
}
