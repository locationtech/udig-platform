package net.refractions.udig.limit;

import static org.junit.Assert.*;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.internal.boundary.BoundaryServiceFactory;

import org.junit.Test;

public class LimitServiceTest {

	private BoundaryServiceFactory limitFactory = new BoundaryServiceFactory();
	private IBoundaryService boundaryService = limitFactory.create(IBoundaryService.class, null, null);
	
	@Test
	public void testFactory() {
		//System.out.println(this.limitService.getClass().toString());
		assertNotNull(this.boundaryService);
	}

	@Test
	public void testGetExtent() {
		assertNull(this.boundaryService.getExtent());
	}
	
	@Test
	public void testSetStrategy() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetLimit() {
		assertNull(this.boundaryService.getBoundary());
	}

	@Test
	public void testGetCrs() {
		assertNull(this.boundaryService.getCrs());
	}

}
