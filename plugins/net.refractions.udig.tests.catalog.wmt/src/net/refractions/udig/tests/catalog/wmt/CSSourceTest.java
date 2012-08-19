package net.refractions.udig.tests.catalog.wmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.catalog.internal.wmt.wmtsource.CSSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;

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
