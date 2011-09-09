package net.refractions.udig.limit;

import static org.junit.Assert.*;
import net.refractions.udig.internal.limit.LimitServiceFactory;

import org.junit.Test;

public class LimitServiceTest {

	private LimitServiceFactory limitFactory = new LimitServiceFactory();
	private ILimitService limitService = limitFactory.create(ILimitService.class, null, null);
	
	@Test
	public void testFactory() {
		//System.out.println(this.limitService.getClass().toString());
		assertNotNull(this.limitService);
	}

	@Test
	public void testGetExtent() {
		assertNull(this.limitService.getExtent());
	}
	
	@Test
	public void testSetStrategy() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetLimit() {
		assertNull(this.limitService.getLimit());
	}

	@Test
	public void testGetCrs() {
		assertNull(this.limitService.getCrs());
	}

}
