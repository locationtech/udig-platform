/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.wmsc.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import net.refractions.udig.catalog.tests.wmsc.Activator;

import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.Specification;
import org.geotools.data.wms.WMS1_1_1;
import org.geotools.ows.ServiceException;
import org.junit.BeforeClass;
import org.junit.Test;

public class TileImageReadWriterTest {
    
    static URL serverURL = null; // default to offline
    
    @BeforeClass 
    public static void available() throws Exception {
        URL url = new URL( "http://localhost:8080/geoserver/ows?service=wms&version=1.3.0&request=GetCapabilities");
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            int responseCode = connection.getResponseCode();
            if( 200 == responseCode ){
                serverURL = url;
            }
            else {
                System.out.println("Unable to run TileImageReadWriterTest: HTTP Response Code "+responseCode+" "+url);
                serverURL = null;
            }
        }
        catch( ConnectException refused ){
            System.out.println("Unable to run TileImageReadWriterTest: "+refused );
            serverURL = null;
        }
        
    }
    @Test
    public void testIsStale() throws Exception {
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance); //$NON-NLS-1$
        
        if(serverURL == null ) return; // skip as we are offline
        MockTiledWebMapServer service = new MockTiledWebMapServer( serverURL );
        
        TileImageReadWriter tileReadWriter = new TileImageReadWriter(service, "resources"); //$NON-NLS-1$

        WMSTileSet tileSet = new WMSTileSet();
        tileSet.setLayers("tasmania"); //$NON-NLS-1$
        tileSet.setCoorindateReferenceSystem("WGS84(DD)"); //$NON-NLS-1$
        tileSet.setFormat("image/png"); //$NON-NLS-1$
        
        WMSTile tile = new WMSTile(service, tileSet, null, 0.009332133792713271);
        tile.setPosition("0_0"); //$NON-NLS-1$

        Boolean state = tileReadWriter.isTileStale(tile, "png"); //$NON-NLS-1$

        assertTrue(state);
    }

    @Test
    public void testIsNotStale() throws Exception {
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance); //$NON-NLS-1$

        if(serverURL == null ) return; // skip as we are offline
        MockTiledWebMapServer service = new MockTiledWebMapServer( serverURL );
        
        TileImageReadWriter tileReadWriter = new TileImageReadWriter(service, "resources"); //$NON-NLS-1$

        WMSTileSet tileSet = new WMSTileSet();
        tileSet.setLayers("tasmania"); //$NON-NLS-1$
        tileSet.setCoorindateReferenceSystem("WGS84(DD)"); //$NON-NLS-1$
        tileSet.setFormat("image/png"); //$NON-NLS-1$
        
        WMSTile tile = new WMSTile(service, tileSet, null, 0.009332133792713271);
        tile.setPosition("1_0"); //$NON-NLS-1$

        Boolean state = tileReadWriter.isTileStale(tile, "png"); //$NON-NLS-1$

        assertFalse(state);
    }
    
    class MockTiledWebMapServer extends AbstractOpenWebService<WMSCCapabilities, TileSet> {

        public MockTiledWebMapServer( URL serverURL ) throws IOException, ServiceException {
            super(serverURL);
            // TODO Auto-generated constructor stub
        }

        @Override
        public WMSCCapabilities getCapabilities() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected ServiceInfo createInfo() {
            return new MockWMSCInfo();
        }

        @Override
        protected ResourceInfo createInfo( TileSet resource ) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void setupSpecifications() {
            specs = new Specification[1];
            specs[0] = new WMS1_1_1();
        }
        
    }
    
    protected class MockWMSCInfo implements ServiceInfo {

        @Override
        public String getDescription() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<String> getKeywords() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URI getPublisher() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URI getSchema() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public URI getSource() {
            try {
                return serverURL.toURI();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String getTitle() {
            // TODO Auto-generated method stub
            return null;
        }

        
    }
}
