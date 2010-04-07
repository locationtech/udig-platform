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
package net.refractions.udig.catalog.ui.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Takes a FeatureCollection with features with MultiPointand Point and converts them all to 
 * MultiPoint and returns the features.  
 * 
 * @author Jesse
 * @since 1.1.0
 */
class ToMultiPointFeatureCollection extends AbstractGeometryTransformingFeatureCollection{

    public ToMultiPointFeatureCollection( FeatureCollection<SimpleFeatureType, SimpleFeature> source, SimpleFeatureType schema, GeometryDescriptor typeToUseAsGeometry, 
            MathTransform mt, IProgressMonitor currentMonitor ) {
        super(source, schema, typeToUseAsGeometry, mt, currentMonitor);
    }

    @Override
    protected Geometry toCollection( Geometry geometry ) {
        if( geometry instanceof Point ){
            Point point=(Point) geometry;
            GeometryFactory factory=point.getFactory();
            return factory.createMultiPoint(new Point[]{point});
        }
        return geometry;
    }

}
