package net.refractions.udig.project.internal;

import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;

public class MapInterceptorsTest extends AbstractProjectTestCase {

    @Override
    protected void setUp() throws Exception {
        TestMapCreationInterceptor.mapCreated=null;
        TestMapOpeningInterceptor.mapOpening=null;
    }

    public void testMapInterceptors() throws Exception {
        assertNull(TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);

        Map map=MapTests.createDefaultMap("name", 1, true, null); //$NON-NLS-1$

        assertEquals(map, TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);

        TestMapCreationInterceptor.mapCreated=null;

        Layer createLayer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(createLayer);

        assertNull(TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);


        map.getLayersInternal().remove(createLayer);

        assertNull(TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);


        assertNull(TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);

    }

}
