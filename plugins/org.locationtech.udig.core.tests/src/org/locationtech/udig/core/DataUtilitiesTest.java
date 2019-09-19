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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DataUtilitiesTest {
    
    @Test
    public void testCompareEquality() throws Exception{
    	SimpleFeatureType featureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,timestamp:java.util.Date,val:Double");

    	SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,timestamp:java.util.Date,val:Integer");

    	int compare = DataUtilities.compare(featureType, targetFeatureType);
    	
    	assertEquals(0, compare);
    }
    
    
    @Test
    public void testCompareEqualReorder() throws Exception{
    	SimpleFeatureType featureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,val:Double,timestamp:java.util.Date");

    	SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,timestamp:java.util.Date,val:Double");

    	int compare = DataUtilities.compare(featureType, targetFeatureType);
    	
    	assertEquals(1, compare);
    }
    
    
    @Test
    public void testCompareNotEqual2() throws Exception{
       	SimpleFeatureType featureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,timestamp:java.util.Date,val:Double");

    	SimpleFeatureType targetFeatureType = DataUtilities.createType("testType",
    			"geometry:Point,Name:String,timestamp:java.util.Date,val1:Integer");

    	int compare = DataUtilities.compare(featureType, targetFeatureType);
    	
    	assertEquals(-1, compare);
    }
    
}
