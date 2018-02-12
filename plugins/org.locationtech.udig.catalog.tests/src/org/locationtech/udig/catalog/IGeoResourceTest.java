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
        final URL urlWithUserInfo = new URL("http://testuser:testpasswd@localhost:1523");
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
        assertTrue(!displayID.contains("testpasswd"));
        assertTrue(displayID.contains("testuser:**"));
    }
}
