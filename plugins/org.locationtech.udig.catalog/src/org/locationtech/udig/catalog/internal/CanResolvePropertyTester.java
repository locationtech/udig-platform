/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal;

import org.locationtech.udig.catalog.IResolve;

import org.eclipse.core.expressions.PropertyTester;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.ows.wms.WebMapServer;
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
            return resolve.canResolve( org.geotools.ows.wms.Layer.class );            
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
