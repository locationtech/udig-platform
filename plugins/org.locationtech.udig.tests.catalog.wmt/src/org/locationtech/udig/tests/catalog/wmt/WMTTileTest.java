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
import org.locationtech.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;

import org.junit.Test;

public class WMTTileTest {
    
    @Test
    public void testNormalizeDegreeValue() {
        double delta = 0.00000000001;
        
        assertEquals(90, WMTTileFactory.normalizeDegreeValue(90, 90), delta);
        assertEquals(45, WMTTileFactory.normalizeDegreeValue(45, 90), delta);
        assertEquals(-90, WMTTileFactory.normalizeDegreeValue(-90, 90), delta);
        assertEquals(-45, WMTTileFactory.normalizeDegreeValue(-45, 90), delta);
        assertEquals(0, WMTTileFactory.normalizeDegreeValue(180, 90), delta);
    }
}
