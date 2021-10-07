/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2021, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

public class IGeoResourceTest {
    @Test
    public void testGetDisplayIDWithUserInfoHasNoPassword() throws Exception {
        final URL urlWithUserInfo = new URL("http://testuser:testpasswd@localhost:1523"); //$NON-NLS-1$
        IGeoResource geoResourceMock = new IGeoResource() {

            @Override
            public Status getStatus() {
                return null;
            }

            @Override
            public Throwable getMessage() {
                return null;
            }

            @Override
            public URL getIdentifier() {
                return urlWithUserInfo;
            }

            @Override
            protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
                return null;
            }
        };

        String displayID = geoResourceMock.getDisplayID();

        assertNotNull(displayID);
        assertTrue(!displayID.contains("testpasswd")); //$NON-NLS-1$
        assertTrue(displayID.contains("testuser:**")); //$NON-NLS-1$
    }
}
