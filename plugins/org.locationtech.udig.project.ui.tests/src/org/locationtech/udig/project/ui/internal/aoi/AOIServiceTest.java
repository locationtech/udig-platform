/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.aoi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.aoi.AOIProxy;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.internal.aoi.AOIServiceFactory;

public class AOIServiceTest {

    private IAOIService aOIService = null;

    @Before
    public void testService() {
        AOIServiceFactory aoiFactory = new AOIServiceFactory();
        assertNotNull(aoiFactory);
        aOIService = aoiFactory.create(IAOIService.class, null, null);
        assertNotNull(aOIService);
    }

    @Test
    public void testCRSStrategy() {
        AOIProxy proxy = aOIService.findProxy("org.locationtech.udig.project.ui.crsAOI"); //$NON-NLS-1$
        aOIService.setProxy(proxy);
        String id = aOIService.getProxy().getId();
        assertEquals("org.locationtech.udig.project.ui.crsAOI", id); //$NON-NLS-1$

        assertProxyValuesMatchingService(proxy, aOIService);
    }

    @Test
    public void testScreenStrategy() {
        AOIProxy proxy = aOIService.findProxy("org.locationtech.udig.project.ui.screenAOI"); //$NON-NLS-1$
        aOIService.setProxy(proxy);
        String id = aOIService.getProxy().getId();
        assertEquals("org.locationtech.udig.project.ui.screenAOI", id); //$NON-NLS-1$

        assertProxyValuesMatchingService(proxy, aOIService);
    }

    private void assertProxyValuesMatchingService(AOIProxy proxy, IAOIService aOIService2) {
        assertEquals(proxy.getExtent(), aOIService2.getExtent());
        assertEquals(proxy.getGeometry(), aOIService2.getGeometry());
        assertEquals(proxy.getCrs(), aOIService2.getCrs());
    }

}
