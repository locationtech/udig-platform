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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.ResourceInfo;
import org.geotools.data.Transaction;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * For testing.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class TestFeatureStore implements FeatureStore<SimpleFeatureType, SimpleFeature> {

	public Name getName(){
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public List<FeatureId> addFeatures(FeatureReader<SimpleFeatureType, SimpleFeature> reader) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public List<FeatureId> addFeatures(FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public Transaction getTransaction() {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void modifyFeatures(AttributeDescriptor[] type, Object[] value,
			Filter filter) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void modifyFeatures(AttributeDescriptor type, Object value, Filter filter)
			throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void removeFeatures(Filter filter) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void setFeatures(FeatureReader<SimpleFeatureType, SimpleFeature> reader) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void setTransaction(Transaction transaction) {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void addFeatureListener(FeatureListener listener) {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public ReferencedEnvelope getBounds() throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public org.geotools.geometry.jts.ReferencedEnvelope getBounds(Query query) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public int getCount(Query query) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public DataStore getDataStore() {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(Query query) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(Filter filter) throws IOException {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public SimpleFeatureType getSchema() {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public void removeFeatureListener(FeatureListener listener) {
		throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
	}

	public Set getSupportedHints() {
		return Collections.EMPTY_SET;
	}

    public ResourceInfo getInfo() {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }

    public QueryCapabilities getQueryCapabilities() {
        throw new IllegalStateException("This should not be called"); //$NON-NLS-1$
    }
}
