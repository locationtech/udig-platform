/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.tests.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.refractions.udig.catalog.URLUtils;

import org.eclipse.core.runtime.Platform;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the default catalog
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class CatalogImplTest {

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
}
