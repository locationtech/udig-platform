package net.refractions.udig.tests.catalog.wmt;

import net.refractions.udig.catalog.internal.wmt.wmtsource.CSSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;
import junit.framework.TestCase;

public class CSSourceTest extends TestCase {
    
    public void testInit() {
        CSSource csSource = (CSSource) WMTSourceFactory.createSource(null, 
                WMTSource.getRelatedServiceUrl(CSSource.class), 
                "tile.openstreetmap.org/{z}/{x}/{y}.png/2/18", true);
        
        assertEquals(19, csSource.getScaleList().length);
        assert(Double.isNaN(csSource.getScaleList()[0]));
        assert(Double.isNaN(csSource.getScaleList()[1]));
        assert(!Double.isNaN(csSource.getScaleList()[2]));
        
        assertEquals("http://tile.openstreetmap.org/0/1/2.png", csSource.getTileUrl(0, 1, 2));
    }

}
