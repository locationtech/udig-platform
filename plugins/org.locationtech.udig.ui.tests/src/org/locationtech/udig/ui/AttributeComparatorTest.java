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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    public static SimpleFeatureType featurType;
    private SimpleFeature featureNameNullIdOne;
    private SimpleFeature featureNameNotNullIdTwo; 
    
    @BeforeClass
    public static void beforeClass() throws Throwable {
        featurType = DataUtilities.createType("type", "name:String,id:int");
    }
    
    @Before
    public void setUp() {
        final String name2 = "name2"; //$NON-NLS-1$

        featureNameNullIdOne = SimpleFeatureBuilder.build(featurType, new Object[]{null, 1}, "1");

        featureNameNotNullIdTwo = SimpleFeatureBuilder.build(featurType, new Object[]{name2, 2},"2");

    }
    /**
     * Test method for
     * {@link org.locationtech.udig.ui.AttributeComparator#compare(org.geotools.feature.SimpleFeature, org.geotools.feature.SimpleFeature)}.
     * 
     * @throws Throwable
     */
    @Test
    public void testCompare() throws Throwable {
        ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>(2);

        features.add(featureNameNotNullIdTwo);
        features.add(featureNameNullIdOne);

        sort(features, featureNameNotNullIdTwo, featureNameNullIdOne, "name");
        sort(features, featureNameNullIdOne, featureNameNotNullIdTwo, "id");
    }

    private void sort( ArrayList<SimpleFeature> features, SimpleFeature feature1, SimpleFeature feature2, String xpath ) {
        Collections.sort(features, new AttributeComparator(SWT.UP, xpath));

        assertEquals(feature2, features.get(0));
        assertEquals(feature1, features.get(1));

        Collections.sort(features, new AttributeComparator(SWT.DOWN, xpath));

        assertEquals(feature1, features.get(0));
        assertEquals(feature2, features.get(1));
    }

    /**
     * compare features where an attribute of one object is null, expects to 
     */
    @Test
    public void testCompareNullWithNonNullStringAttribute() {

        AttributeComparator nameUpComparator = new AttributeComparator(SWT.UP, "name");
        assertEquals(-1, nameUpComparator.compare(featureNameNullIdOne, featureNameNotNullIdTwo));
        assertEquals(1, nameUpComparator.compare(featureNameNotNullIdTwo, featureNameNullIdOne));

        AttributeComparator nameDownComparator = new AttributeComparator(SWT.DOWN, "name");
        assertEquals(1, nameDownComparator.compare(featureNameNullIdOne, featureNameNotNullIdTwo));
        assertEquals(-1, nameDownComparator.compare(featureNameNotNullIdTwo, featureNameNullIdOne));
    }

    @Test
    public void testCompareNullWithNonNullIntAttribute() {

        AttributeComparator nameUpComparator = new AttributeComparator(SWT.UP, "id");
        assertEquals(1, nameUpComparator.compare(featureNameNullIdOne, featureNameNotNullIdTwo));
        assertEquals(-1, nameUpComparator.compare(featureNameNotNullIdTwo, featureNameNullIdOne));

        AttributeComparator nameDownComparator = new AttributeComparator(SWT.DOWN, "id");
        assertEquals(-1, nameDownComparator.compare(featureNameNullIdOne, featureNameNotNullIdTwo));
        assertEquals(1, nameDownComparator.compare(featureNameNotNullIdTwo, featureNameNullIdOne));
    }

    @Test
    public void testCompareWithSameObject() {
        
        AttributeComparator nameComparator = new AttributeComparator(SWT.UP, "name");
        
        assertEquals(0, nameComparator.compare(featureNameNullIdOne, featureNameNullIdOne));
        
    }
}
