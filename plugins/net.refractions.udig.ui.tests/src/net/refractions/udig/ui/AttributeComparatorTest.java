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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Test the atributeComparator
 * @author Jesse
 * @since 1.1.0
 */
@SuppressWarnings("nls")
public class AttributeComparatorTest {

    /**
     * Test method for
     * {@link net.refractions.udig.ui.AttributeComparator#compare(org.geotools.feature.SimpleFeature, org.geotools.feature.SimpleFeature)}.
     * 
     * @throws Throwable
     */
    @Test
    public void testCompare() throws Throwable {
       SimpleFeatureType type = DataUtilities.createType("type", "name:String,id:int");

        ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>(2);

        final String name1 = null;
        final String name2 = "name2"; //$NON-NLS-1$
        SimpleFeature feature1 = SimpleFeatureBuilder.build(type, new Object[]{name1, 1}, "1");

        SimpleFeature feature2 = SimpleFeatureBuilder.build(type, new Object[]{name2, 2},"2");

        features.add(feature1);
        features.add(feature2);

        sort(features, feature1, feature2, "name"); //$NON-NLS-1$
        sort(features, feature1, feature2, "id"); //$NON-NLS-1$
    }

    private void sort( ArrayList<SimpleFeature> features, SimpleFeature feature1, SimpleFeature feature2, String xpath ) {
        Collections.sort(features, new AttributeComparator(SWT.UP, xpath));

        assertEquals(feature2, features.get(0));
        assertEquals(feature1, features.get(1));

        Collections.sort(features, new AttributeComparator(SWT.DOWN, xpath));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));
    }

}
