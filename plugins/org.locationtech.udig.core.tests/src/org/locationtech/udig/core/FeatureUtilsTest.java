package org.locationtech.udig.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class FeatureUtilsTest {
    static SimpleFeatureType featureType;

    @BeforeClass
    public static void beforeClass() throws SchemaException {
        featureType = DataUtilities.createType("testType",
                "geometry:Point,Name:String,timestamp:java.util.Date,name:String");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertyNameFeatureTypeNull() {
        FeatureUtils.getActualPropertyName(null, "a property");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertyNamePropertyNull() {
        String nullString = null;
        FeatureUtils.getActualPropertyName(featureType, nullString);
    }

    @Test
    public void testActalPropertyNameUpperLowerCaseFirstMatchingAttribut() {
        assertEquals("Name", FeatureUtils.getActualPropertyName(featureType, "NaMe"));
    }

    @Test
    public void testAccessEqualsOverEqualsIgnoreCaseValueOfFeature() {
        SimpleFeatureBuilder featureTypeBuilder = new SimpleFeatureBuilder(featureType);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

        featureTypeBuilder
                .addAll(new Object[] { geometryFactory.createPoint(new Coordinate(10, 10)), "first",
                        new Date(), "second" });
        SimpleFeature feature = featureTypeBuilder.buildFeature("id");

        assertEquals("second",
                feature.getAttribute(FeatureUtils.getActualPropertyName(featureType, "name")));
    }

    @Test
    public void  testActualPropertyWithNameThatDoesNotExists() {
        assertNull(FeatureUtils.getActualPropertyName(featureType, "whatever"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testActualPropertiesNamesNullList() {
        List<String> propertyNames = null;
        FeatureUtils.getActualPropertyName(featureType,propertyNames);
    }

    @Test
    public void testActualPropertiesNamesEmptyList() {
        List<String> propertyNames = Collections.emptyList();
        List<String> result = FeatureUtils.getActualPropertyName(featureType,propertyNames);
        assertTrue(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualPropertiesFeatureTypeNull() {
        List<String> propertyNames = Collections.emptyList();
        FeatureUtils.getActualPropertyName(null, propertyNames);
    }

    @Test
    public void testActualPropertiesUpperLowerCaseFirstMatchingAttribut() {
        List<String> propertyNames = Arrays.asList("name", "Name");
        List<String> result = FeatureUtils.getActualPropertyName(featureType, propertyNames);

        assertTrue(result.indexOf("name") < result.indexOf("Name"));
    }

    @Test
    public void testActualPropertiesRequestPropertyThatDoesNotExtsis() {
        List<String> propertyNames = Arrays.asList("name", "Whatever");
        List<String> result = FeatureUtils.getActualPropertyName(featureType, propertyNames);
        assertFalse(result.contains("Whatever"));
        assertTrue(result.contains("name"));
    }
}
