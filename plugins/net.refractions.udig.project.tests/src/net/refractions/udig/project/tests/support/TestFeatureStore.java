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

import java.io.IOException;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * For testing.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class TestFeatureStore implements FeatureStore {

    public Set addFeatures( FeatureReader reader ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public Set addFeatures( FeatureCollection collection ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public Transaction getTransaction() {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void modifyFeatures( AttributeType[] type, Object[] value, Filter filter )
            throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void modifyFeatures( AttributeType type, Object value, Filter filter )
            throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void removeFeatures( Filter filter ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void setFeatures( FeatureReader reader ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void setTransaction( Transaction transaction ) {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void addFeatureListener( FeatureListener listener ) {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public Envelope getBounds() throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public Envelope getBounds( Query query ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public int getCount( Query query ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public DataStore getDataStore() {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public FeatureCollection getFeatures() throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public FeatureCollection getFeatures( Query query ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public FeatureCollection getFeatures( Filter filter ) throws IOException {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public FeatureType getSchema() {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public void removeFeatureListener( FeatureListener listener ) {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

}
