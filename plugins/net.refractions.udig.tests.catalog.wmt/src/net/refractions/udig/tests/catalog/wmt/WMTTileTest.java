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
package net.refractions.udig.tests.catalog.wmt;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;

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
