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
package net.refractions.udig.project.ui.internal.aoi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.refractions.udig.aoi.AOIProxy;
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
	public void testCRSStrategy() {
        AOIProxy proxy = aOIService.findProxy("net.refractions.udig.project.ui.crsAOI");
        aOIService.setProxy(proxy);
        String id = aOIService.getProxy().getId();
        assertEquals("net.refractions.udig.project.ui.crsAOI", id);
        
        assertNull(aOIService.getExtent());
        assertNull(aOIService.getGeometry());
        assertNull(aOIService.getCrs());
	}

	@Test
	public void testScreenStrategy() {
        
	    AOIProxy proxy = aOIService.findProxy("net.refractions.udig.project.ui.screenAOI");
        aOIService.setProxy(proxy);
        String id = aOIService.getProxy().getId();
        assertEquals("net.refractions.udig.project.ui.screenAOI", id);
        
        assertNull(aOIService.getExtent());
        assertNull(aOIService.getGeometry());
        assertNull(aOIService.getCrs());
	}
	
}
