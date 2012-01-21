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

/**
 * @author Mauricio Pazos
 *
 */
final class Util {
	
	private Util(){}

	/**
	 * Get the features contained on the envelope/s. If there are more than one
	 * envelope, create a {@link Filter} of those envelopes and return the
	 * features contained in it.
	 * 
	 * @param bbox
	 * @param context
	 * @return List of {@link SimpleFeature}}
	 * @throws IOException
	 */
	public static List<SimpleFeature> retrieveFeaturesInBBox(Envelope bbox, IToolContext context) throws IOException {
		List<Envelope> list = new LinkedList<Envelope>(); 
		list.add(bbox);
		return retrieveFeaturesInBBox(list, context);
	}

	public static List<SimpleFeature> retrieveFeaturesInBBox(Filter filter, ILayer layer) throws IOException {

		SimpleFeatureSource source = (SimpleFeatureSource) layer.getResource(FeatureSource.class, null);

		String typename = source.getSchema().getName().toString();

		Query query = new Query(typename, filter);

		SimpleFeatureCollection features = source.getFeatures(query);

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

		FeatureSource source = selectedLayer.getResource(FeatureSource.class, null);

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
