package net.refractions.udig.catalog.wmsc.server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.WMSCapabilities;
import org.junit.Test;

public class WMSCParserTest {

    private static class MockHttpResponse implements HTTPResponse{

        private final InputStream in;
        private final String contentType;

        public MockHttpResponse(final InputStream in, final String contentType){
            this.in = in;
            this.contentType = contentType;
        }
        
        @Override
        public void dispose() {
            try{
                in.close();
            }catch(Exception e){
                //ignore, dispose() could be called multiple times
            }
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getResponseHeader( String arg0 ) {
            return null;
        }

        @Override
        public InputStream getResponseStream() throws IOException {
            return in;
        }
        
    }
    @Test
    public void testGeoWebCache() throws Exception {
        URL url = WMSCParserTest.class.getResource("wmscCapabilities3.xml");
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
    @Test
    public void testEsriMapCapabilities() throws Exception {
        URL url = WMSCParserTest.class.getResource("esrimapCapabilities.xml");
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
        URL url = WMSCParserTest.class.getResource("tiledVendorSpecificNested.xml");
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
