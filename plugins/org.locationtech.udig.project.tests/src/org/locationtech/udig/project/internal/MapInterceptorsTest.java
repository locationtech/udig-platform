/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.tests.support.MapTests;

public class MapInterceptorsTest extends AbstractProjectTestCase {

    @Before
    public void setUp() throws Exception {
        TestMapCreationInterceptor.mapCreated = null;
        TestMapOpeningInterceptor.mapOpening = null;
    }

    @Test
    public void testMapInterceptors() throws Exception {
        assertNull(TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);

        Map map = MapTests.createDefaultMap("name", 1, true, null); //$NON-NLS-1$

        assertEquals(map, TestMapCreationInterceptor.mapCreated);
        assertNull(TestMapOpeningInterceptor.mapOpening);

        TestMapCreationInterceptor.mapCreated = null;

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
