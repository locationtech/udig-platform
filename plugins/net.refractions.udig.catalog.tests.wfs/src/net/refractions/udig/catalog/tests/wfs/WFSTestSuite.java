package net.refractions.udig.catalog.tests.wfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.refractions.udig.catalog.tests.internal.wfs.WFSGeoResourceTest;
import net.refractions.udig.catalog.tests.internal.wfs.WFSServiceTest;

public class WFSTestSuite extends TestSuite {

    /** Returns the suite. This is required to use the JUnit Launcher. */
    public static Test suite() {
        return new WFSTestSuite();
    }

    public WFSTestSuite() {
        addTest(new TestSuite(WFSGeoResourceTest.class));
        addTest(new TestSuite(WFSServiceTest.class));
    }

}
