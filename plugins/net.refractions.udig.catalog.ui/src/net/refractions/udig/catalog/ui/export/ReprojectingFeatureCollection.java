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
import net.refractions.udig.ui.ProgressFeatureCollection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.spatialschema.geometry.MismatchedDimensionException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Reprojects the features that as they are read from the collection.  The features are read only so don't try to attempt to
 * set any values on the features.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class ReprojectingFeatureCollection extends ProgressFeatureCollection
        implements
            FeatureCollection {

    private FeatureType featureType;
    private MathTransform mt;

    /**
     * new instance
     * @param delegate the feature collection to transform
     * @param monitor the monitor to update
     * @param featureType the featureType of the <em>final</em> featureType.  Which means that the default geometry attribute
     * type declares the projection <em>after</em> the transformation.
     * @param mt
     */
    public ReprojectingFeatureCollection( FeatureCollection delegate, IProgressMonitor monitor,
            FeatureType featureType, MathTransform mt ) {
        super(delegate, monitor);
        this.mt=mt;
        this.featureType=featureType;
    }

    @Override
    protected Iterator openIterator() {
        final Iterator iterator = delegate.iterator();
        return new Iterator(){

            private FeatureWrapper feature;

            public boolean hasNext() {
                while( feature == null ) {
                    if( !iterator.hasNext() )
                        return false;
                    Feature next = (Feature) iterator.next();
                    if( next==null )
                        continue;
                    Geometry geometry = next.getDefaultGeometry();
                    if( geometry!=null ){
	                    try {
	                        geometry = JTS.transform(geometry, mt);
	                    } catch (MismatchedDimensionException e) {
	                        throw (RuntimeException) new RuntimeException().initCause(e);
	                    } catch (TransformException e) {
	                        throw (RuntimeException) new RuntimeException(
	                                Messages.ReprojectingFeatureCollection_transformationError + next.getID()).initCause(e);
	                    }
                    }
                    feature = new FeatureWrapper(next, featureType, new Geometry[]{geometry},
                    		new String[]{ featureType.getDefaultGeometry().getName()});
                }
                return feature!=null;
            }

            public Object next() {
                monitor.worked(1);
                FeatureWrapper tmp = feature;
                feature=null;
                return tmp;
            }

            public void remove() {
                iterator.next();
            }

        };
    }


}
