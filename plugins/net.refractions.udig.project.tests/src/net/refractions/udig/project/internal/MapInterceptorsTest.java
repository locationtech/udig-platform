/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;

import org.junit.Before;
import org.junit.Test;

public class MapInterceptorsTest extends AbstractProjectTestCase {

    @Before
    public void setUp() throws Exception {
        TestMapCreationInterceptor.mapCreated=null;
        TestMapOpeningInterceptor.mapOpening=null;
    }
    
    @Test
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
