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
package net.refractions.udig.project.tests;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceInterceptor;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Ignore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

@Ignore
public class TestInterceptorPost implements IResourceInterceptor<Object> {
    public static volatile int runs=0;
    public static boolean changeType=false;
    public Object run( ILayer layer, Object resource,Class<? super Object> requestedType ) {
        runs++;
        if( changeType)
            return new FeatureSource<SimpleFeatureType, SimpleFeature>(){

                public void addFeatureListener( FeatureListener arg0 ) {
                }

                public ReferencedEnvelope getBounds() throws IOException {
                    return null;
                }

                public ReferencedEnvelope getBounds( Query arg0 ) throws IOException {
                    return null;
                }

                public int getCount( Query arg0 ) throws IOException {
                    return 0;
                }

                public DataStore getDataStore() {
                    return null;
                }

                public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() throws IOException {
                    return null;
                }

                public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures( Query arg0 ) throws IOException {
                    return null;
                }

                public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures( Filter arg0 ) throws IOException {
                    return null;
                }

                public SimpleFeatureType getSchema() {
                    return null;
                }

                public void removeFeatureListener( FeatureListener arg0 ) {
                }
            
            	public Set getSupportedHints() {
            		return Collections.EMPTY_SET;
            	}

                public ResourceInfo getInfo() {
                    return null;
                }

				public Name getName() {
					return null;
				}

                public QueryCapabilities getQueryCapabilities() {
                    return null;
                }
        };
        return resource;
    }

}
