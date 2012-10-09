/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.tool.info.internal;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.tool.info.LayerPointInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DataStoreDescribeLayer {
    
    /**
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl#createInfo(java.awt.Point)
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