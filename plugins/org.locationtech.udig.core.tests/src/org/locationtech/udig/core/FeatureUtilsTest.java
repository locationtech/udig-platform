package org.locationtech.udig.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

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
}
