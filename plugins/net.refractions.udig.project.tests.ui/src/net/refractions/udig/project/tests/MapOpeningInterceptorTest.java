package net.refractions.udig.project.tests;

import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.TestMapOpeningInterceptor;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

public class MapOpeningInterceptorTest extends AbstractProjectUITestCase {

    protected void setUp() throws Exception {
        super.setUp();
        TestMapOpeningInterceptor.mapOpening=null;
    }


    public void testMapOpeningInterceptor() throws Exception {
        assertNull(TestMapOpeningInterceptor.mapOpening);
        Map createDefaultMap = MapTests.createDefaultMap("ftn", 3, true, null); //$NON-NLS-1$
        ApplicationGIS.openMap(createDefaultMap);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return TestMapOpeningInterceptor.mapOpening!=null;
            }

        }, false);

        assertEquals(createDefaultMap, TestMapOpeningInterceptor.mapOpening);
    }
}
