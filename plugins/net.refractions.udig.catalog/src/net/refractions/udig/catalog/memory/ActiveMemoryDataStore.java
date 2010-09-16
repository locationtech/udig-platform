/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.memory.internal.MemoryServiceListener;

import org.geotools.data.FeatureReader;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * This is an extended MemoryDataStore which provides proper event
 * notification. Clients wishing to supply their own sub-class
 * of MemoryDataStore for use in the MemoryService extension
 * should sub-class this class, as that will permit the Catalog
 * to be notified by events.
 * 
 * @author mleslie
 * @author rgould
 * @since 0.6.0
 */
public class ActiveMemoryDataStore extends MemoryDataStore {
    List<MemoryServiceListener> list = new ArrayList<MemoryServiceListener>();

    /**
     * Construct <code>AnotherMemoryDataStore</code>.
     *
     */
    public ActiveMemoryDataStore() {
        super();
    }

    /**
     * Construct <code>AnotherMemoryDataStore</code>.
     *
     * @param collection
     */
    public ActiveMemoryDataStore( FeatureCollection<SimpleFeatureType, SimpleFeature> collection ) {
        super(collection);
    }

    /**
     * Construct <code>AnotherMemoryDataStore</code>.
     *
     * @param array
     */
    public ActiveMemoryDataStore( SimpleFeature[] array ) {
        super(array);
    }

    /**
     * Construct <code>AnotherMemoryDataStore</code>.
     *
     * @param reader
     * @throws IOException
     */
    public ActiveMemoryDataStore( FeatureReader<SimpleFeatureType, SimpleFeature> reader ) throws IOException {
        super(reader);
    }
    
    /**
     * TODO summary sentence for addFeatureListener ...
     * 
     * @param listener
     */
    public void addListener(MemoryServiceListener listener) {
        this.list.add(listener);
    }
    
    /**
     * TODO summary sentence for removeFeatureListener ...
     * 
     * @param listener
     * @return true if removed
     */
    public boolean removeListener(MemoryServiceListener listener) {
        return this.list.remove(listener);
    }
    
    public void createSchema(SimpleFeatureType featureType) throws IOException {
       super.createSchema(featureType);
       for(MemoryServiceListener listener : this.list) {
           listener.schemaChanged();
       }
    }
    
    public void removeSchema( String typeName ) {
        schema.remove(typeName);
        memory.remove(typeName);
        for(MemoryServiceListener listener : this.list) {
            listener.schemaChanged();
        }
    }

}
