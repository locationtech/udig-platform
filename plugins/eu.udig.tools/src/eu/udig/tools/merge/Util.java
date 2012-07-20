/**
 * 
 */
package eu.udig.tools.merge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.tool.IToolContext;

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

import com.vividsolutions.jts.geom.Envelope;

import eu.udig.tools.internal.ui.util.LayerUtil;

/**
 * @author Mauricio Pazos
 *
 */
public final class Util {
	
	private Util(){}

	/**
	 * Retrieves the features from layer using the filter
	 * 
	 * @param filter
	 * @param layer
	 * 
	 * @return List of {@link SimpleFeature}}
	 * @throws IOException
	 */
//	public static List<SimpleFeature> retrieveFeatures(Filter filter, ILayer layer) throws IOException {
//
//		SimpleFeatureSource source = (SimpleFeatureSource) layer.getResource(FeatureSource.class, null);
//
//		String typename = source.getSchema().getName().toString();
//
//		Query query = new Query(typename, filter);
//
//		//SimpleFeatureCollection features = source.getFeatures(query);
//		SimpleFeatureCollection features = source.getFeatures(); //FIXME Hack done to continue the merge tool devel (It is necessary to solve this issue)
//		
//		List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
//		FeatureIterator<SimpleFeature> iter = null;
//		try {
//			iter = features.features();
//			while (iter.hasNext()) {
//				SimpleFeature f = iter.next();
//				featureList.add(f);
//			}
//		} finally {
//			if (iter != null) {
//				iter.close();
//			}
//		}
//		return featureList;
//	}
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
		// FIXME HACK FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures(query);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = source.getFeatures();

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
