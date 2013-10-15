/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.ui.export;

import java.util.Iterator;

import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.ui.ProgressFeatureCollection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

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
        FeatureCollection<SimpleFeatureType, SimpleFeature> {

    private SimpleFeatureType featureType;
    private MathTransform mt;

    /**
     * new instance
     * @param delegate the feature collection to transform
     * @param monitor the monitor to update
     * @param featureType the featureType of the <em>final</em> featureType.  Which means that the default geometry attribute 
     * type declares the projection <em>after</em> the transformation.
     * @param mt
     */
    public ReprojectingFeatureCollection( FeatureCollection<SimpleFeatureType, SimpleFeature> delegate, IProgressMonitor monitor, 
            SimpleFeatureType SimplefeatureType, MathTransform mt ) {
        super(delegate, monitor);
        this.mt=mt;
        this.featureType=SimplefeatureType;
    }
    
    @Override
    protected Iterator<SimpleFeature> openIterator() {
        final FeatureIterator<SimpleFeature> iterator = delegate.features();
        return new Iterator<SimpleFeature>(){

            private FeatureWrapper feature;

            public boolean hasNext() {
                while( feature == null ) {
                    if( !iterator.hasNext() )
                        return false;
                    SimpleFeature next = iterator.next();
                    if( next==null )
                        continue;
                    Geometry geometry = (Geometry) next.getDefaultGeometry();
                    if( geometry!=null ){
	                    try {
	                        geometry = JTS.transform(geometry, mt);
	                    } catch (TransformException e) {
	                        throw (RuntimeException) new RuntimeException(
	                                Messages.ReprojectingFeatureCollection_transformationError + next.getID()).initCause(e);
	                    }
                    }
                    GeometryDescriptor defaultGeometry2 = featureType.getGeometryDescriptor();
                    Name name = defaultGeometry2.getName();
                    String localPart = name.getLocalPart();
                    feature = new FeatureWrapper(next, featureType, new Geometry[]{geometry}, 
                    		new String[]{ localPart});
                }
                return feature!=null;
            }

            public SimpleFeature next() {
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
