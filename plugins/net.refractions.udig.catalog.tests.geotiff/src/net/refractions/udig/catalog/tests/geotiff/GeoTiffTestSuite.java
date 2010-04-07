package net.refractions.udig.catalog.tests.geotiff;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.refractions.udig.catalog.tests.internal.geotiff.GeoTiffGeoResourceTest;
import net.refractions.udig.catalog.tests.internal.geotiff.GeoTiffServiceTest;

public class GeoTiffTestSuite extends TestSuite {

    /** Returns the suite. This is required to use the JUnit Launcher. */
    public static Test suite() {
        return new GeoTiffTestSuite();
    }

    public GeoTiffTestSuite() {
        addTest(new TestSuite(GeoTiffGeoResourceTest.class));
        addTest(new TestSuite(GeoTiffServiceTest.class));
    }

}
