package net.refractions.udig.catalog.ui.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Ensures that the geometry/multigeometry is a multigeometry because that's all shapefiles accept 
 * @author jesse
 *
 */
class ToMultiPolygonFeatureCollection extends
		AbstractGeometryTransformingFeatureCollection {

    public ToMultiPolygonFeatureCollection( FeatureCollection<SimpleFeatureType, SimpleFeature> source, SimpleFeatureType schema, GeometryDescriptor typeToUseAsGeometry, MathTransform mt, IProgressMonitor monitor ) {
        super(source, schema, typeToUseAsGeometry, mt, monitor);
    }

	@Override
	protected Geometry toCollection(Geometry geometry) {
		if (geometry instanceof Polygon) {
			Polygon polygon = (Polygon) geometry;
			GeometryFactory factory = polygon.getFactory();
			return factory.createMultiPolygon(new Polygon[] { polygon });
		}
		return geometry;
	}

}
