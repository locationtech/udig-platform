/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Test FIDComparator class
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class FIDComparatorTest {

    @Test
    public void testCompare() throws Throwable {
        GeometryFactory fac = new GeometryFactory();
        SimpleFeatureType type = DataUtilities.createType("type", "geom:Point,name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>(2);

        final String name1 = "name1"; //$NON-NLS-1$
        final String name2 = "name2"; //$NON-NLS-1$
        SimpleFeature feature1 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), name1,
                1}, "ID1"); //$NON-NLS-1$

        SimpleFeature feature2 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                2}, "ID2"); //$NON-NLS-1$

        features.add(feature1);
        features.add(feature2);

        Collections.sort(features, new FIDComparator(SWT.UP));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));

        Collections.sort(features, new FIDComparator(SWT.DOWN));

        assertEquals(feature2, features.get(0));
        assertEquals(feature1, features.get(1));
        

        SimpleFeature feature11 = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                11}, "ID11"); //$NON-NLS-1$
        
        features.add( feature11 );

        Collections.sort(features, new FIDComparator(SWT.UP));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));
        assertEquals(feature11, features.get(2));

        Collections.sort(features, new FIDComparator(SWT.DOWN));

        assertEquals(feature1, features.get(2));
        assertEquals(feature2, features.get(1));
        assertEquals(feature11, features.get(0));
        
        features.clear();

        features.add( feature11 );
        features.add( feature1 );
        
        Collections.sort(features, new FIDComparator(SWT.DOWN));
        assertEquals(feature1, features.get(1));
        assertEquals(feature11, features.get(0));

        features.clear();

        features.add( feature1 );
        SimpleFeature featureStrange = SimpleFeatureBuilder.build(type, new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                11}, "Blarg2"); //$NON-NLS-1$
        features.add(featureStrange);
        Collections.sort(features, new FIDComparator(SWT.DOWN));
        assertEquals(featureStrange, features.get(1));
        assertEquals(feature1, features.get(0));
        
    }

    
}
