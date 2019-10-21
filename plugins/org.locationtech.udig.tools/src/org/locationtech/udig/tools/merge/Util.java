/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.tool.IToolContext;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Or;

import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.tools.internal.ui.util.LayerUtil;

/**
 * @author Mauricio Pazos
 *
 */
public final class Util {
	
	private Util(){}

	public static List<SimpleFeature> retrieveFeatures(Filter filter, ILayer layer) throws IOException {

		FeatureCollection<SimpleFeatureType, SimpleFeature> features = LayerUtil.getSelectedFeatures(layer, filter);
		
		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
		FeatureIterator<SimpleFeature> iter = null;
		try {
			iter = features.features();
			while (iter.hasNext()) {
				SimpleFeature f = iter.next();
				featureList.add(f);
			}
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
		return featureList;
	}
	
	
	
	public static List<SimpleFeature> retrieveFeaturesInBBox(List<Envelope> bbox, IToolContext context) throws IOException {

		ILayer selectedLayer = context.getSelectedLayer();

		FeatureSource<SimpleFeatureType, SimpleFeature> source = selectedLayer.getResource(FeatureSource.class, null);

		String typename = source.getSchema().getName().toString();

		// creates the query with a bbox filter
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);

		Filter filter = selectedLayer.createBBoxFilter(bbox.get(0), null);
		Filter mergedFilter;
		Or filterOR = null;
		for (int index = 0; index < bbox.size(); index++) {

			mergedFilter = selectedLayer.createBBoxFilter(bbox.get(index), null);
			filterOR = ff.or(filter, mergedFilter);
		}

		Query query = new Query(typename, filterOR);

		// retrieves the feature in the bbox
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(query);

		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
		FeatureIterator<SimpleFeature> iter = null;
		try {
			iter = features.features();
			while (iter.hasNext()) {
				SimpleFeature f = iter.next();
				featureList.add(f);
			}
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
		return featureList;
	}
	
}
