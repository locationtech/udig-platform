/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2015, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IService;

/**
 * Tests for sorted services
 *
 * @author HendrikPeilke
 */
public class ServiceSortingTest {

    @Test
    public void testServiceComparison() throws Exception {
        ICatalog ci = CatalogPlugin.getDefault().getLocalCatalog();
        IService service = ci.acquire(new URL("http://www.randomurl.com"),
                new NullProgressMonitor());
        assertTrue(service instanceof MoreInterestingService.MoreInterestingServiceImpl);
    }
}
