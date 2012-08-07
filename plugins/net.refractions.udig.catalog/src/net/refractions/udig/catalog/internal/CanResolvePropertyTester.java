/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal;

import net.refractions.udig.catalog.IResolve;

import org.eclipse.core.expressions.PropertyTester;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.wms.WebMapServer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.Style;

/**
 * PropertyTester used to check IResolve support for common resolve targets.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class CanResolvePropertyTester extends PropertyTester {
    
    private static final String PROPERTY_FEATURESOURCE = "featureSource"; //$NON-NLS-1$
    private static final String PROPERTY_FEATURESTORE = "featureStore"; //$NON-NLS-1$
    private static final String PROPERTY_DATASTORE = "dataSource"; //$NON-NLS-1$
    private static final String PROPERTY_WMS = "wms"; //$NON-NLS-1$
    private static final String PROPERTY_WMS_LAYER = "wmsLayer"; //$NON-NLS-1$
    private static final String PROPERTY_SCHEMA = "schema";  //$NON-NLS-1$
    private static final String PROPERTY_STYLE = "style";  //$NON-NLS-1$
    
    public CanResolvePropertyTester() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
     */
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        IResolve resolve = (IResolve) receiver;
        if( resolve == null ){
            return false;
        }
        // that is the class equivelent of "instanceof"
        if( PROPERTY_FEATURESOURCE.equals(property)){
            return resolve.canResolve( SimpleFeatureSource.class );            
        }
        else if( PROPERTY_FEATURESTORE.equals(property)){
            return resolve.canResolve( SimpleFeatureStore.class );            
        }
        else if( PROPERTY_DATASTORE.equals(property)){
            return resolve.canResolve( DataStore.class );            
        }
        else if( PROPERTY_WMS.equals(property)){
            return resolve.canResolve( WebMapServer.class );            
        }
        else if( PROPERTY_WMS_LAYER.equals(property)){
            return resolve.canResolve( org.geotools.data.ows.Layer.class );            
        }
        else if( PROPERTY_SCHEMA.equals(property)){
            return resolve.canResolve( SimpleFeatureType.class );            
        }
        else if( PROPERTY_STYLE.equals(property)){
            return resolve.canResolve( Style.class );            
        }
        return false;
    }

}
