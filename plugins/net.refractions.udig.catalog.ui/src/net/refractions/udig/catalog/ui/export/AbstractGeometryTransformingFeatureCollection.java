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

import java.util.Iterator;

import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.collection.AbstractFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Takes a FeatureCollection with features with MultiPolygons and Polygons and converts them all to
 * MultiPolygons and returns the features.
 *
 * @author Jesse
 * @since 1.1.0
 */
abstract class AbstractGeometryTransformingFeatureCollection extends AbstractFeatureCollection {

    private final FeatureCollection source;
    private final FeatureType schema;
    private final GeometryAttributeType typeToUseAsGeometry;
    private final IProgressMonitor monitor;
    private final MathTransform mt;

    /**
     * new instance
     * @param source the source features collection
     * @param schema the schema to use to create new features.
     * @param typeToUseAsGeometry the attribute that will be used as the geometry.
     * @param mt the math transform to use to transform the geometries from the source projection to the destination projection
     * @param monitor2 progress monitor
     */
    public AbstractGeometryTransformingFeatureCollection( FeatureCollection source, FeatureType schema,
            GeometryAttributeType typeToUseAsGeometry, MathTransform mt, IProgressMonitor monitor2) {
        super(schema);

        this.source=source;
        this.schema=schema;
        this.typeToUseAsGeometry=typeToUseAsGeometry;
        this.monitor=monitor2;
        this.mt=mt;
    }

    @Override
    protected void closeIterator( Iterator close ) {
        source.close(close);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterator<Feature> openIterator() {
        final Iterator<Feature> iter=source.iterator();
        return new Iterator<Feature>(){

            private Feature feature;

            public boolean hasNext() {
                while( feature==null ){
                    if ( !iter.hasNext() )
                        return false;
                    Feature next = iter.next();
                    if( next==null )
                        continue;
                    Geometry geometry=(Geometry) next.getAttribute(typeToUseAsGeometry.getName());
                    geometry = toCollection(geometry);
                    if( geometry!=null ){
	                    try {
	                        geometry = JTS.transform(geometry, mt);
	                    } catch (MismatchedDimensionException e) {
	                        throw (RuntimeException) new RuntimeException( ).initCause( e );
	                    } catch (TransformException e) {
	                        throw (RuntimeException) new RuntimeException( Messages.ReprojectingFeatureCollection_transformationError+next.getID()).initCause( e );
	                    }
                    }
                    feature = new FeatureWrapper(next, schema, new Geometry[]{geometry},
                    		new String[]{ schema.getDefaultGeometry().getName()});
                }

                return feature!=null;
            }

            public Feature next() {
                Feature tmp = feature;
                feature=null;
                monitor.worked(1);
                return tmp;
            }

            public void remove() {
                iter.remove();
            }

        };
    }

    /**
     * Method should ensure that the geometry is a GeometryCollection of the correct type.  For example a polygon should be
     * converted into a Multi Polygon.
     */
    protected abstract Geometry toCollection( Geometry geometry );

    @Override
    public int size() {
        return source.size();
    }

}
