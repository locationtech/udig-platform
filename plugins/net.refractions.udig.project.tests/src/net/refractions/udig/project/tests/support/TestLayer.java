/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.tests.support;

import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.impl.LayerImpl;

import org.junit.Ignore;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Provides some setter methods that otherwise are not available.
 * 
 * @author jones
 * @since 1.1.0
 */
@Ignore
public class TestLayer extends LayerImpl {

    private List<IGeoResource> resources;

    public TestLayer( ) {
    }

    /**
     * Sets the resources of the layer to be resources.  If called then a list that does not have events will be returned.
     *   
     * @param resources resources of layer.
     */
    public TestLayer( List<IGeoResource> resources) {
        this.resources=resources;
    }

    SimpleFeatureType schema=null;
    
    @Override
    public SimpleFeatureType getSchema() {
        if(schema == null  )
            return super.getSchema();
        return schema;
    }

    /**
     * @param schema The schema to set.
     */
    public void setSchema( SimpleFeatureType schema ) {
        this.schema = schema;
    }
    
    @Override
    public List<IGeoResource> getGeoResources() {
        if( resources==null )
            return super.getGeoResources();
        
        return resources;
    }
    
    @Override
    public String toString() {
        return "Name: "+getName(); //$NON-NLS-1$
    }
    
}
