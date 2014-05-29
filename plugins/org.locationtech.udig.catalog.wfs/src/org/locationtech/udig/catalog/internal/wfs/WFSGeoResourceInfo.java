/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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

import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.ui.graphics.Glyph;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.ResourceInfo;
import org.geotools.data.wfs.impl.WFSContentDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

class WFSGeoResourceInfo extends IGeoResourceInfo {

    /**
     * 
     */
    private final WFSGeoResourceImpl wfsResource;
    CoordinateReferenceSystem crs = null;
    private boolean writable = false;
    
    WFSGeoResourceInfo(WFSGeoResourceImpl wfsGeoResourceImpl) throws IOException {
        wfsResource = wfsGeoResourceImpl;
        
        // extract Type Name first for use in logs
        name = wfsResource.typename;
        if( name == null ){
            // consider illegal state exception?
            WfsPlugin.trace("typename not provided",null);
        }
        
        WFSContentDataStore ds = wfsResource.parent.getDS(null);
        
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = ds
                .getFeatureSource(wfsResource.typename);
        
        writable = featureSource instanceof FeatureStore;

        ResourceInfo resourceInfo = featureSource.getInfo();
        SimpleFeatureType ft = null;
        try {
            ft = ds.getSchema(wfsResource.typename);
        } catch (Exception crippled) {
            // unable to handle the describe feature type response for this typename
            WfsPlugin.log("Unable to handle DescribeFeatureType for "+name+":"+crippled, crippled);
        }
        bounds = resourceInfo.getBounds();
        // relax bounds for wfs ...
        // bounds = ReferencedEnvelopeCache.getReferencedEnvelope( crs );
        if( bounds == null){
            WfsPlugin.trace("Bounds not provided for "+name,null);
        }
        // Metadata
        description = resourceInfo.getDescription();
        title = resourceInfo.getTitle();

        crs = resourceInfo.getCRS();
        if (crs == null && ft != null) {
            WfsPlugin.trace("CRS not provided for "+name+" ... trying feature type",null);
            crs = ft.getCoordinateReferenceSystem();
        }
        if (crs == null && bounds != null) {
            WfsPlugin.trace("CRS not provided for "+name+" ... trying bounds",null);
            crs = bounds.getCoordinateReferenceSystem();
        }
        if( crs == null ){
            WfsPlugin.trace("CRS not provided for "+name,null);
        }
        // Looking up appropriate schema namespace
        schema = resourceInfo.getSchema();
        if (schema == null && ft != null) {
            Name featureTypeName = ft.getName();
            if (featureTypeName != null && featureTypeName.getNamespaceURI() != null) {
                String namespaceURI = featureTypeName.getNamespaceURI();
                try {
                    schema = namespaceURI != null ? new URI(namespaceURI) : null;
                } catch (URISyntaxException e) {
                    WfsPlugin.trace("namespaceURI for "+name+" not valid" + e, e);
                }
            }
        }
        if (schema == null) {
            // assume parent schema
            WfsPlugin.trace("Assuming namespace from WFS service endpoint. Assumption may produce invalid GML.",null);
            schema = wfsResource.parent.getID().toURI();
        }
        Set<String> tags = new TreeSet<String>();
        try {
            tags.addAll(resourceInfo.getKeywords());
        } catch (Throwable t) {
            WfsPlugin.trace("Could not retrieve keywords", t); //$NON-NLS-1$
            // no keywords for you
        }
        tags.addAll(Arrays.asList(new String[]{"wfs", wfsResource.typename})); //$NON-NLS-1$
        keywords = tags.toArray(new String[0]);
        
        // generate default icon
        icon = Glyph.icon(ft);
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResourceInfo#getCRS()
     */
    public CoordinateReferenceSystem getCRS() {
        if (crs != null)
            return crs;
        return super.getCRS();
    }

    public boolean isWritable() {
        return writable;
    }
}
