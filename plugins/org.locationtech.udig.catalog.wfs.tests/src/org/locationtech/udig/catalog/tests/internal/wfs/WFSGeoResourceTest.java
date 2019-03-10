/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal.wfs;

import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceExtension;
import org.locationtech.udig.catalog.tests.AbstractGeoResourceTest;
import org.locationtech.udig.catalog.util.CatalogTestUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Before;

/**
 * @author dzwiers
 */
public class WFSGeoResourceTest extends AbstractGeoResourceTest {

    private IService service = null;
    private IGeoResource resource = null;

    @Before
    public void setUp() throws Exception {
        WFSServiceExtension fac = new WFSServiceExtension();
        URL url = new URL("https://demo.geo-solutions.it/geoserver/wfs?"); //$NON-NLS-1$
        CatalogTestUtils.assumeNoConnectionException(url, 3000);
        service = fac.createService(url, fac.createParams(url));
        resource = service.resources((IProgressMonitor) null).get(0);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractGeoResourceTest#getResolve()
     */
    protected IGeoResource getResolve() {
        return resource;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return true;
    }
    
}
