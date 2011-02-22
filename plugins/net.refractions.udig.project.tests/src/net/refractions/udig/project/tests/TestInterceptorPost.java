package net.refractions.udig.project.tests;

import java.io.IOException;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

public class TestInterceptorPost implements IResourceInterceptor<Object> {
    public static volatile int runs=0;
    public static boolean changeType=false;
    public Object run( ILayer layer, Object resource,Class<? super Object> requestedType ) {
        runs++;
        if( changeType)
            return new FeatureSource(){

                public void addFeatureListener( FeatureListener arg0 ) {
                }

                public Envelope getBounds() throws IOException {
                    return null;
                }

                public Envelope getBounds( Query arg0 ) throws IOException {
                    return null;
                }

                public int getCount( Query arg0 ) throws IOException {
                    return 0;
                }

                public DataStore getDataStore() {
                    return null;
                }

                public FeatureCollection getFeatures() throws IOException {
                    return null;
                }

                public FeatureCollection getFeatures( Query arg0 ) throws IOException {
                    return null;
                }

                public FeatureCollection getFeatures( Filter arg0 ) throws IOException {
                    return null;
                }

                public FeatureType getSchema() {
                    return null;
                }

                public void removeFeatureListener( FeatureListener arg0 ) {
                }

        };
        return resource;
    }

}
