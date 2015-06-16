/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;

/**
 * Tests for the default catalog
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class CatalogImplTest {
    
    public static String SERVICE_COMPARISON_TEST_URL = "http://www.randomurl.com"; //$NON-NLS-1$

    @Ignore
    @Test
    public void testUrlEquals() throws Exception {
        if (Platform.getOS() == Platform.OS_WIN32) {
            assertTrue(URLUtils.urlEquals(new URL("file://c:\\java/udig/"), //$NON-NLS-1$
                    new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
        }
        if (Platform.getOS() == Platform.OS_WIN32) {
            assertFalse(URLUtils.urlEquals(new URL("file://d:\\java/udig/"), //$NON-NLS-1$
                    new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
        }
        if (Platform.getOS() == Platform.OS_WIN32) {
            assertTrue(URLUtils.urlEquals(new URL("file:///C:/java/udig"), //$NON-NLS-1$
                    new URL("file:/C:\\java\\udig"), false)); //$NON-NLS-1$
        }
        assertTrue(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig"), false)); //$NON-NLS-1$
        assertFalse(URLUtils.urlEquals(new URL("file:///Java/udig"), //$NON-NLS-1$
                null, false));
        assertFalse(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig#ResourceName"), false)); //$NON-NLS-1$
        assertTrue(URLUtils.urlEquals(new URL("file:///java/udig"), //$NON-NLS-1$
                new URL("file:/java/udig#ResourceName"), true)); //$NON-NLS-1$
    }
    
    @Test
    public void testAquire() {
        
    }
    
    @Test
    public void testServiceComparison() throws Exception {
        ICatalog ci = CatalogPlugin.getDefault().getLocalCatalog();
        IService service = ci.acquire(new URL(SERVICE_COMPARISON_TEST_URL), new NullProgressMonitor());
        assertTrue(service instanceof MoreInterestingService.MoreInterestingServiceImpl);
    }
}
