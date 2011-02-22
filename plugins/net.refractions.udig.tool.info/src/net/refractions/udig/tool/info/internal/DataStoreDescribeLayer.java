package net.refractions.udig.tool.info.internal;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.AdaptableFeature;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.tool.info.LayerPointInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.filter.AttributeExpression;
import org.geotools.filter.BBoxExpression;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.GeometryFilter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class DataStoreDescribeLayer {

    /**
     * @see net.refractions.udig.project.internal.render.impl.RendererImpl#getInfo(java.awt.Point)
     */
    public static List<LayerPointInfo> info( final ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws Exception{
        List<Feature> features = info2( layer, bbox, monitor );
        List<LayerPointInfo> list = new ArrayList<LayerPointInfo>( features.size() );
        for( Feature feature :  features  ) {
            final Feature feature2 = feature;
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

    public static List<Feature> info2( ILayer layer, ReferencedEnvelope bbox, IProgressMonitor monitor ) throws Exception{
        FeatureSource source = layer.getResource( FeatureSource.class, null );
        FeatureType type = source.getSchema();
        CoordinateReferenceSystem crs = layer.getCRS();

        if( !bbox.getCoordinateReferenceSystem().equals( crs )) {
            bbox = bbox.transform(crs, true);
        }
        FilterFactory factory = FilterFactoryFinder.createFilterFactory();

        BBoxExpression theBBox = factory.createBBoxExpression( bbox );
        AttributeExpression theGeom = factory.createAttributeExpression(type.getDefaultGeometry().getName() );

        GeometryFilter filter = factory.createGeometryFilter( GeometryFilter.GEOMETRY_INTERSECTS );
        filter.addLeftGeometry( theGeom );
        filter.addRightGeometry( theBBox );

        layer.getQuery(false);
        final FeatureCollection results = source.getFeatures( filter );
//        if( results.getCount() == 0 ) {
//            return null; // no content!
//        }

        List<Feature> list = new ArrayList<Feature>();
        FeatureIterator reader = results.features();
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
