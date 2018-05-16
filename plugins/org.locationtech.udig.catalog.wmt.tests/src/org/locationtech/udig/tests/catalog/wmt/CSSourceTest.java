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
package org.locationtech.udig.tests.catalog.wmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.CSSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;

import org.junit.Test;

public class CSSourceTest {
    
    @Test
    public void testInit() {
        CSSource csSource = (CSSource) WMTSourceFactory.createSource(null, 
                WMTSource.getRelatedServiceUrl(CSSource.class), 
                "tile.openstreetmap.org/{z}/{x}/{y}.png/2/18", true);
        
        assertEquals(19, csSource.getScaleList().length);
        assertTrue(Double.isNaN(csSource.getScaleList()[0]));
        assertTrue(Double.isNaN(csSource.getScaleList()[1]));
        assertTrue(!Double.isNaN(csSource.getScaleList()[2]));
        
        assertEquals("http://tile.openstreetmap.org/0/1/2.png", csSource.getTileUrl(0, 1, 2));
    }

}
