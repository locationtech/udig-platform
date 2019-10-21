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
package org.locationtech.udig.tool.info.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.project.AdaptableFeature;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.tool.info.LayerPointInfo;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class DataStoreDescribeLayer {
    
    /**
     * @see org.locationtech.udig.project.internal.render.impl.RendererImpl#createInfo(java.awt.Point)
     */
    public static List<LayerPointInfo> info( final ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws Exception{
        List<SimpleFeature> features = info2( layer, bbox, monitor );
        List<LayerPointInfo> list = new ArrayList<LayerPointInfo>( features.size() );
        for( SimpleFeature feature :  features  ) {
            final SimpleFeature feature2 = feature;
            LayerPointInfo info =  new LayerPointInfo( layer ){                    
                public String getMimeType() {
                    return LayerPointInfo.GML;
                }
                public Object acquireValue() {
                    return feature2;
                }            
            };
            list.add( info );
        }
        return list;
    }
    
    public static List<SimpleFeature> info2( ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws Exception{
        FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource( FeatureSource.class, null );
        SimpleFeatureType type = source.getSchema();
        CoordinateReferenceSystem crs = layer.getCRS();
        
        if( !bbox.getCoordinateReferenceSystem().equals( crs )) {
            bbox = bbox.transform(crs, true);
        }
        FilterFactory2 factory = (FilterFactory2) CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Geometry geom = new GeometryFactory().toGeometry(bbox);
        Intersects filter = factory.intersects(factory.property(type.getGeometryDescriptor().getName()), factory.literal(geom));
        
        layer.getQuery(false);
        final FeatureCollection<SimpleFeatureType, SimpleFeature>  results = source.getFeatures( filter );
//        if( results.getCount() == 0 ) {
//            return null; // no content!
//        }

        List<SimpleFeature> list = new ArrayList<SimpleFeature>();
        FeatureIterator<SimpleFeature> reader = results.features();
        try {
            while( reader.hasNext() ) {
                if( monitor != null && monitor.isCanceled() ) return list;                
                list.add( new AdaptableFeature( reader.next(), layer) );
            }
        }
        finally {
            reader.close();
        }
        return list;
    }
}
