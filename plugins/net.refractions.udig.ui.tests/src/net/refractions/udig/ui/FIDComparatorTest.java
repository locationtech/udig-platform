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
package net.refractions.udig.ui;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import junit.framework.TestCase;

/**
 * Test FIDComparator class
 *
 * @author Jesse
 * @since 1.1.0
 */
public class FIDComparatorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCompare() throws Throwable {
        GeometryFactory fac = new GeometryFactory();
        FeatureType type = DataUtilities.createType("type", "geom:Point,name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        ArrayList<Feature> features = new ArrayList<Feature>(2);

        final String name1 = "name1"; //$NON-NLS-1$
        final String name2 = "name2"; //$NON-NLS-1$
        Feature feature1 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), name1,
                1}, "ID1"); //$NON-NLS-1$

        Feature feature2 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                2}, "ID2"); //$NON-NLS-1$

        features.add(feature1);
        features.add(feature2);

        Collections.sort(features, new FIDComparator(SWT.UP));

        assertEquals(feature2, features.get(0));
        assertEquals(feature1, features.get(1));

        Collections.sort(features, new FIDComparator(SWT.DOWN));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));


        Feature feature11 = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                11}, "ID11"); //$NON-NLS-1$

        features.add( feature11 );

        Collections.sort(features, new FIDComparator(SWT.UP));

        assertEquals(feature11, features.get(0));
        assertEquals(feature2, features.get(1));
        assertEquals(feature1, features.get(2));

        Collections.sort(features, new FIDComparator(SWT.DOWN));

        assertEquals(feature11, features.get(2));
        assertEquals(feature2, features.get(1));
        assertEquals(feature1, features.get(0));

        features.clear();

        features.add( feature11 );
        features.add( feature1 );

        Collections.sort(features, new FIDComparator(SWT.DOWN));
        assertEquals(feature1, features.get(0));
        assertEquals(feature11, features.get(1));

        features.clear();

        features.add( feature1 );
        Feature featureStrange = type.create(new Object[]{fac.createPoint(new Coordinate(10, 10)), name2,
                11}, "Blarg2"); //$NON-NLS-1$
        features.add(featureStrange);
        Collections.sort(features, new FIDComparator(SWT.DOWN));
        assertEquals(featureStrange, features.get(0));
        assertEquals(feature1, features.get(1));

    }


}
