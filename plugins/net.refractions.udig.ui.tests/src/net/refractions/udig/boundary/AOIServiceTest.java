package net.refractions.udig.boundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.refractions.udig.aoi.IAOIService;
import net.refractions.udig.internal.aoi.AOIServiceFactory;

import org.junit.Before;
import org.junit.Test;


public class AOIServiceTest {

	private AOIServiceFactory aoiFactory = null;
	private IAOIService aOIService = null;
	
	@Before
	public void testService() {
	    aoiFactory = new AOIServiceFactory();
	    assertNotNull(aoiFactory);
	    aOIService = aoiFactory.create(IAOIService.class, null, null);
		assertNotNull(aOIService);
		
		// used to list the existing strategies
		/*for (BoundaryProxy proxy : boundaryService.getProxyList()) {
		    System.out.println(proxy.getId());
		}*/
	}

	@Test
	public void testAllStrategy() {
        aOIService.setProxy(aOIService.findProxy("net.refractions.udig.ui.aoiAll"));
        String id = aOIService.getProxy().getId();
        assertEquals("net.refractions.udig.ui.aoiAll", id);
        
        assertNull(aOIService.getExtent());
        assertNull(aOIService.getGeometry());
        assertNull(aOIService.getCrs());
	}
	
	@Test
	public void testCRSStrategy() {
        aOIService.setProxy(aOIService.findProxy("net.refractions.udig.project.ui.crsAOI"));
        String id = aOIService.getProxy().getId();
        assertEquals("net.refractions.udig.project.ui.crsAOI", id);
        
        assertNull(aOIService.getExtent());
        assertNull(aOIService.getGeometry());
        assertNull(aOIService.getCrs());
	}

	@Test
	public void testScreenStrategy() {
        aOIService.setProxy(aOIService.findProxy("net.refractions.udig.project.ui.screenAOI"));
        String id = aOIService.getProxy().getId();
        assertEquals("net.refractions.udig.project.ui.screenAOI", id);
        
        assertNull(aOIService.getExtent());
        assertNull(aOIService.getGeometry());
        assertNull(aOIService.getCrs());
	}
	
}
