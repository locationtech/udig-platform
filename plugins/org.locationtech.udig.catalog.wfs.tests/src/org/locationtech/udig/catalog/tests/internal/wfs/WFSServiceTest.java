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

import org.junit.BeforeClass;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceExtension;
import org.locationtech.udig.catalog.tests.AbstractServiceTest;
import org.locationtech.udig.catalog.util.CatalogTestUtils;

/**
 * @author dzwiers
 */
public class WFSServiceTest extends AbstractServiceTest {

    private static IService service = null;

    @BeforeClass
    public static void setUp() throws Exception {
        WFSServiceExtension fac = new WFSServiceExtension();
        URL url = new URL("https://demo.geo-solutions.it/geoserver/wfs?"); //$NON-NLS-1$
        CatalogTestUtils.assumeNoConnectionException(url, 3000);
        service = fac.createService(url, fac.createParams(url));
    }

    @Override
    protected IService getResolve() {
        return service;
    }

    @Override
    protected boolean hasParent() {
        return false;
    }

    @Override
    protected boolean isLeaf() {
        return false;
    }

}
