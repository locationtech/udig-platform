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

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.geotools.data.DataUtilities;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;

/**
 * Test the atributeComparator
 * @author Jesse
 * @since 1.1.0
 */
public class AttributeComparatorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for
     * {@link net.refractions.udig.ui.AttributeComparator#compare(org.geotools.feature.Feature, org.geotools.feature.Feature)}.
     *
     * @throws Throwable
     */
    public void testCompare() throws Throwable {
        FeatureType type = DataUtilities.createType("type", "name:String,id:int"); //$NON-NLS-1$ //$NON-NLS-2$

        ArrayList<Feature> features = new ArrayList<Feature>(2);

        final String name1 = null;
        final String name2 = "name2"; //$NON-NLS-1$
        Feature feature1 = type.create(new Object[]{name1, 1});

        Feature feature2 = type.create(new Object[]{name2, 2});

        features.add(feature1);
        features.add(feature2);

        sort(features, feature1, feature2, "name"); //$NON-NLS-1$
        sort(features, feature1, feature2, "id"); //$NON-NLS-1$
    }

    private void sort( ArrayList<Feature> features, Feature feature1, Feature feature2, String xpath ) {
        Collections.sort(features, new AttributeComparator(SWT.UP, xpath));

        assertEquals(feature2, features.get(0));
        assertEquals(feature1, features.get(1));

        Collections.sort(features, new AttributeComparator(SWT.DOWN, xpath));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));
    }

}
