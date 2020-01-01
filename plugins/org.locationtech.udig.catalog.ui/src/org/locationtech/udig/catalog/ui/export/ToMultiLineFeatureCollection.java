/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.export;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

/**
 * Takes a FeatureCollection with features with MultiPointand Point and converts them all to 
 * MultiPoint and returns the features.  
 * 
 * @author Jesse
 * @since 1.1.0
 */
class ToMultiLineFeatureCollection extends AbstractGeometryTransformingFeatureCollection{

    public ToMultiLineFeatureCollection( 
            SimpleFeatureCollection source, SimpleFeatureType schema, 
            GeometryDescriptor typeToUseAsGeometry, 
            MathTransform mt, IProgressMonitor monitor ) {
        
        super(source, schema, typeToUseAsGeometry, mt, monitor);
    }

    @Override
    protected Geometry toCollection( Geometry geometry ) {
        if( geometry instanceof LineString ){
            LineString line=(LineString) geometry;
            GeometryFactory factory=line.getFactory();
            return factory.createMultiLineString(new LineString[]{line});
        }
        return geometry;
    }

}
