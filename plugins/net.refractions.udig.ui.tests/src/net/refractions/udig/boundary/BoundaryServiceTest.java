package net.refractions.udig.boundary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.refractions.udig.internal.boundary.BoundaryServiceFactory;

import org.junit.Before;
import org.junit.Test;


public class BoundaryServiceTest {

	private BoundaryServiceFactory boundaryFactory = null;
	private IBoundaryService boundaryService = null;
	
	@Before
	public void testService() {
	    boundaryFactory = new BoundaryServiceFactory();
	    assertNotNull(boundaryFactory);
	    boundaryService = boundaryFactory.create(IBoundaryService.class, null, null);
		assertNotNull(boundaryService);
		
		// used to list the existing strategies
		/*for (BoundaryProxy proxy : boundaryService.getProxyList()) {
		    System.out.println(proxy.getId());
		}*/
	}

	@Test
	public void testAllStrategy() {
        boundaryService.setProxy(boundaryService.findProxy("net.refractions.udig.ui.boundaryAll"));
        String id = boundaryService.getProxy().getId();
        assertEquals("net.refractions.udig.ui.boundaryAll", id);
        
        assertNull(boundaryService.getExtent());
        assertNull(boundaryService.getGeometry());
        assertNull(boundaryService.getCrs());
	}
	
	@Test
	public void testCRSStrategy() {
	    BoundaryProxy boundary = boundaryService.findProxy("net.refractions.udig.project.ui.boundaryCRS");
	    if( boundary != null ){
            boundaryService.setProxy(boundary);
            String id = boundaryService.getProxy().getId();
            assertEquals("net.refractions.udig.project.ui.boundaryCRS", id);
            
            assertNull(boundaryService.getExtent());
            assertNull(boundaryService.getGeometry());
            assertNull(boundaryService.getCrs());
	    }
	}

	@Test
	public void testScreenStrategy() {
        BoundaryProxy boundary = boundaryService.findProxy("net.refractions.udig.tool.default.boundaryScreen");
        if( boundary != null ){
            boundaryService.setProxy(boundary);
            String id = boundaryService.getProxy().getId();
            assertEquals("net.refractions.udig.tool.default.boundaryScreen", id);
            
            assertNull(boundaryService.getExtent());
            assertNull(boundaryService.getGeometry());
            assertNull(boundaryService.getCrs());
        }
	}
	
}
