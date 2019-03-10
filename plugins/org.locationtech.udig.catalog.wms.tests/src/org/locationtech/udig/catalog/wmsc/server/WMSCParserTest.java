/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.util.http.MockHttpResponse;

public class WMSCParserTest {

    @Test
    public void testGeoWebCache() throws Exception {
        //URL url = WMSCParserTest.class.getResource("wmscCapabilities3.xml");
        InputStream stream = WMSCParserTest.class.getResourceAsStream("wmscCapabilities3.xml");
        // String xml = WMSCCapabilitiesResponse.convertStreamToString(stream);

        // InputStream is = new ByteArrayInputStream(caps_xml.getBytes());
        WMSCCapabilitiesResponse response;

        response = new WMSCCapabilitiesResponse(new MockHttpResponse(stream, "text/xml")); //$NON-NLS-1$
        WMSCCapabilities capabilities = (WMSCCapabilities) response.getCapabilities();

        assertNotNull(capabilities.getCapability().getVSCapabilities());

        ArrayList<WMSTileSet> tiles = capabilities.getCapability().getVSCapabilities().getTiles();
        assertFalse(tiles.isEmpty());

    }

    @Ignore
    @Test
    public void testGeoWebCacheOnline() throws Exception {
        URL url = new URL(
                "http://tiledmarble.org/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=getcapabilities&TILED=true");
        InputStream stream = url.openStream();
        // String xml = WMSCCapabilitiesResponse.convertStreamToString(stream);

        // InputStream is = new ByteArrayInputStream(caps_xml.getBytes());
        WMSCCapabilitiesResponse response;

        response = new WMSCCapabilitiesResponse(new MockHttpResponse(stream, "text/xml")); //$NON-NLS-1$
        WMSCCapabilities capabilities = (WMSCCapabilities) response.getCapabilities();

        assertNotNull(capabilities.getCapability().getVSCapabilities());

        ArrayList<WMSTileSet> tiles = capabilities.getCapability().getVSCapabilities().getTiles();
        assertFalse(tiles.isEmpty());
    }

    @Ignore
    @Test
    public void testGeoWebCacheOnline2() throws Exception {
        URL url = new URL(
                "http://tiledmarble.org/geowebcache/service/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=getcapabilities&TILED=true");

        TiledWebMapServer wms = new TiledWebMapServer(url);
        WMSCCapabilities capabilities = wms.getCapabilities();
        assertNotNull(capabilities.getCapability().getVSCapabilities());

        ArrayList<WMSTileSet> tiles = capabilities.getCapability().getVSCapabilities().getTiles();
        assertFalse(tiles.isEmpty());
    }
    @Ignore // fails when running from maven
    @Test
    public void testEsriMapCapabilities() throws Exception {
        //URL url = WMSCParserTest.class.getResource("esrimapCapabilities.xml");
        InputStream stream = WMSCParserTest.class.getResourceAsStream("esrimapCapabilities.xml");
        // String xml = WMSCCapabilitiesResponse.convertStreamToString(stream);

        // InputStream is = new ByteArrayInputStream(caps_xml.getBytes());
        WMSCCapabilitiesResponse response;

        response = new WMSCCapabilitiesResponse(new MockHttpResponse(stream, "text/xml")); //$NON-NLS-1$
        WMSCCapabilities capabilities = (WMSCCapabilities) response.getCapabilities();

        assertNotNull(capabilities.getCapability().getVSCapabilities());

        ArrayList<WMSTileSet> tiles = capabilities.getCapability().getVSCapabilities().getTiles();
        assertFalse(tiles.isEmpty());
    }
    @Test
    public void testTiledVendorSpecificNested() throws Exception {
        //URL url = WMSCParserTest.class.getResource("tiledVendorSpecificNested.xml");
        InputStream stream = WMSCParserTest.class.getResourceAsStream("tiledVendorSpecificNested.xml");
        // String xml = WMSCCapabilitiesResponse.convertStreamToString(stream);

        // InputStream is = new ByteArrayInputStream(caps_xml.getBytes());
        WMSCCapabilitiesResponse response;

        response = new WMSCCapabilitiesResponse(new MockHttpResponse(stream, "text/xml")); //$NON-NLS-1$
        WMSCCapabilities capabilities = (WMSCCapabilities) response.getCapabilities();

        assertNotNull(capabilities.getCapability().getVSCapabilities());

        ArrayList<WMSTileSet> tiles = capabilities.getCapability().getVSCapabilities().getTiles();
        assertFalse(tiles.isEmpty());
    }
}
