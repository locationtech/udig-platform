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

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceExtension;
import org.locationtech.udig.catalog.tests.AbstractServiceTest;
import org.locationtech.udig.catalog.util.CatalogTestUtils;
import org.junit.Before;

/**
 * @author dzwiers
 */
public class WFSServiceTest extends AbstractServiceTest {

    private IService service = null;
    
    @Before
    public void setUp() throws Exception {
        WFSServiceExtension fac = new WFSServiceExtension();
        URL url = new URL("https://demo.geo-solutions.it/geoserver/wfs?"); //$NON-NLS-1$
        CatalogTestUtils.assumeNoConnectionException(url, 3000);
        service = fac.createService(url, fac.createParams(url));
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractServiceTest#getResolve()
     */
    protected IService getResolve() {
        return service;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractResolveTest#hasParent()
     */
    protected boolean hasParent() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.catalog.tests.AbstractResolveTest#isLeaf()
     */
    protected boolean isLeaf() {
        return false;
    }

}
