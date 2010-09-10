package net.refractions.udig.tests.catalog.wmt;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;


public class WMTTileTest extends TestCase {
    
    public void testNormalizeDegreeValue() {
        double delta = 0.00000000001;
        
        assertEquals(90, WMTTileFactory.normalizeDegreeValue(90, 90), delta);
        assertEquals(45, WMTTileFactory.normalizeDegreeValue(45, 90), delta);
        assertEquals(-90, WMTTileFactory.normalizeDegreeValue(-90, 90), delta);
        assertEquals(-45, WMTTileFactory.normalizeDegreeValue(-45, 90), delta);
        assertEquals(0, WMTTileFactory.normalizeDegreeValue(180, 90), delta);
    }
}
