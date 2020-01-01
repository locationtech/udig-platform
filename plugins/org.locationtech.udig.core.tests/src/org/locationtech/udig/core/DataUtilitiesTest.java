/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2019, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.core;

import static org.junit.Assert.assertEquals;

import org.geotools.data.DataUtilities;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

public class DataUtilitiesTest {
    
    @Test
    public void testCompareEquality() throws Exception{
        SimpleFeatureType featureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Integer");

        SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Integer");

        int compare = DataUtilities.compare(featureType, targetFeatureType);

        assertEquals(0, compare);
    }

    @Test
    public void testCompareEqualReorder() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,val:Double,timestamp:java.util.Date");

        SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Double");

        int compare = DataUtilities.compare(featureType, targetFeatureType);

        assertEquals(1, compare);
    }

    @Test
    public void testCompareNotEqual2() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Double");

        SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,val1:Integer");

        int compare = DataUtilities.compare(featureType, targetFeatureType);

        assertEquals(-1, compare);
    }

}
