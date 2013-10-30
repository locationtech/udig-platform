/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.TestMapOpeningInterceptor;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MapOpeningInterceptorTest extends AbstractProjectUITestCase {

    @Before
    public void setUp() throws Exception {
        TestMapOpeningInterceptor.mapOpening=null;
    }
    
    @Ignore
    @Test
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
