/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
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
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureUtilsTest {
    static SimpleFeatureType featureType;

    @BeforeClass
    public static void beforeClass() throws SchemaException {
        featureType = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,name:String"); //$NON-NLS-1$
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertyNameFeatureTypeNull() {
        FeatureUtils.getActualPropertyName(null, "a property"); //$NON-NLS-1$
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertyNamePropertyNull() {
        String nullString = null;
        FeatureUtils.getActualPropertyName(featureType, nullString);
    }

    @Test
    public void testActalPropertyNameUpperLowerCaseFirstMatchingAttribut() {
        assertEquals("Name", FeatureUtils.getActualPropertyName(featureType, "NaMe")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testAccessEqualsOverEqualsIgnoreCaseValueOfFeature() {
        SimpleFeatureBuilder featureTypeBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        featureTypeBuilder
                .addAll(new Object[] { geometryFactory.createPoint(new Coordinate(10, 10)), "first", //$NON-NLS-1$
                        new Date(), "second" }); //$NON-NLS-1$
        SimpleFeature feature = featureTypeBuilder.buildFeature("id"); //$NON-NLS-1$

        assertEquals("second", //$NON-NLS-1$
                feature.getAttribute(FeatureUtils.getActualPropertyName(featureType, "name"))); //$NON-NLS-1$
    }

    @Test
    public void testActualPropertyWithNameThatDoesNotExists() {
        assertNull(FeatureUtils.getActualPropertyName(featureType, "whatever")); //$NON-NLS-1$
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertiesNamesNullList() {
        List<String> propertyNames = null;
        FeatureUtils.getActualPropertyName(featureType, propertyNames);
    }

    @Test
    public void testActualPropertiesNamesEmptyList() {
        List<String> propertyNames = Collections.emptyList();
        List<String> result = FeatureUtils.getActualPropertyName(featureType, propertyNames);
        assertTrue(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertiesFeatureTypeNull() {
        List<String> propertyNames = Collections.emptyList();
        FeatureUtils.getActualPropertyName(null, propertyNames);
    }

    @Test
    public void testActualPropertiesUpperLowerCaseFirstMatchingAttribut() {
        List<String> propertyNames = Arrays.asList("name", "Name"); //$NON-NLS-1$ //$NON-NLS-2$
        List<String> result = FeatureUtils.getActualPropertyName(featureType, propertyNames);

        assertTrue(result.indexOf("name") < result.indexOf("Name")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testActualPropertiesRequestPropertyThatDoesNotExtsis() {
        List<String> propertyNames = Arrays.asList("name", "Whatever"); //$NON-NLS-1$ //$NON-NLS-2$
        List<String> result = FeatureUtils.getActualPropertyName(featureType, propertyNames);
        assertFalse(result.contains("Whatever")); //$NON-NLS-1$
        assertTrue(result.contains("name")); //$NON-NLS-1$
    }

    @Test
    public void testCopyFeatureDoubleToInt() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Double"); //$NON-NLS-1$

        SimpleFeatureType featureType2 = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Integer"); //$NON-NLS-1$

        SimpleFeatureBuilder featureTypeBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        featureTypeBuilder
                .addAll(new Object[] { geometryFactory.createPoint(new Coordinate(10, 10)), "first", //$NON-NLS-1$
                        new Date(), Double.valueOf(2.2) });
        SimpleFeature feature = featureTypeBuilder.buildFeature("id"); //$NON-NLS-1$
        Map<String, String> attributeMap = FeatureUtils.createAttributeMapping(featureType,
                featureType2);
        Collection<SimpleFeature> result = FeatureUtils.copyFeature(feature, featureType2,
                attributeMap, IdentityTransform.create(1));
        for (SimpleFeature c : result) {
            assertNotNull(c.getAttribute("val")); //$NON-NLS-1$
            assertEquals(Integer.valueOf(2), c.getAttribute("val")); //$NON-NLS-1$
            return;
        }
        fail("Copy feature failed. No feature in collection"); //$NON-NLS-1$

    }

    @Test
    public void testCopyFeatureDouble2BigDecimal() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Double"); //$NON-NLS-1$

        SimpleFeatureType featureType2 = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:java.math.BigDecimal"); //$NON-NLS-1$

        SimpleFeatureBuilder featureTypeBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        featureTypeBuilder
                .addAll(new Object[] { geometryFactory.createPoint(new Coordinate(10, 10)), "first", //$NON-NLS-1$
                        new Date(), Double.valueOf(2.2) });
        SimpleFeature feature = featureTypeBuilder.buildFeature("id"); //$NON-NLS-1$

        Map<String, String> attributeMap = FeatureUtils.createAttributeMapping(featureType,
                featureType2);
        Collection<SimpleFeature> result = FeatureUtils.copyFeature(feature, featureType2,
                attributeMap, IdentityTransform.create(1));
        for (SimpleFeature c : result) {
            assertNotNull(c.getAttribute("val")); //$NON-NLS-1$
            assertEquals(new BigDecimal("2.2"), c.getAttribute("val")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        fail("Copy feature failed. No feature in collection"); //$NON-NLS-1$

    }

    @Test
    public void testCopyFeatureDouble2String() throws Exception {
        SimpleFeatureType featureType = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:Double"); //$NON-NLS-1$

        SimpleFeatureType featureType2 = DataUtilities.createType("testType", //$NON-NLS-1$
                "geometry:Point,Name:String,timestamp:java.util.Date,val:String"); //$NON-NLS-1$

        SimpleFeatureBuilder featureTypeBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        featureTypeBuilder
                .addAll(new Object[] { geometryFactory.createPoint(new Coordinate(10, 10)), "first", //$NON-NLS-1$
                        new Date(), Double.valueOf(2.2) });
        SimpleFeature feature = featureTypeBuilder.buildFeature("id"); //$NON-NLS-1$
        Map<String, String> attributeMap = FeatureUtils.createAttributeMapping(featureType,
                featureType2);
        Collection<SimpleFeature> result = FeatureUtils.copyFeature(feature, featureType2,
                attributeMap, IdentityTransform.create(1));
        for (SimpleFeature c : result) {
            assertNull(c.getAttribute("val")); // null should be set in 'val' since no adaptation is //$NON-NLS-1$
                                               // possible and value is not copied
            return;
        }
        fail("Copy feature failed. No feature in collection"); //$NON-NLS-1$

    }
}
