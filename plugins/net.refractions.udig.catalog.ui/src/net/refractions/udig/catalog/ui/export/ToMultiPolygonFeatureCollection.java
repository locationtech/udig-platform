/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
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
