/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.refractions.udig.catalog.internal.wms.WmsPlugin;

import org.geotools.data.ows.AbstractGetCapabilitiesRequest;
import org.geotools.data.ows.HTTPClient;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.Request;
import org.geotools.data.ows.Response;
import org.geotools.data.ows.SimpleHttpClient;
import org.geotools.ows.ServiceException;


/**
 * TiledWebMapServer is a class representing a WMSC. It is used to access the 
 * Capabilities document and perform requests.
 * 
 * See http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation
 * 
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc)
 * @since 1.1.0
 */
public class TiledWebMapServer {

    /** Capabilities document */
    private WMSCCapabilities capabilities = null;
    
    /** Error connecting */
    private Exception couldNotConnect;
    
    private String getCaps_xml;

    /** URL of WMSC Service */
    private URL service;

    /** interacts with the backend through HTTP */
    private final HTTPClient httpClient;
    
    /**
     * Creates a new service with the given url
     * @param serverURL
     */
    public TiledWebMapServer( URL serverURL ) {
        this.service = serverURL;
        this.httpClient = new SimpleHttpClient();
    }
    
    /**
     * Creates a new service with the given capabilities xml.  If checkForUpdate is true,
     * then it also tries to request a new capabilities document to see if there is 
     * any update it needs (if it can't connect it just continues with the given caps).
     * 
     * @param serverURL
     * @param caps_xml
     * @param checkForUpdate
     */
    public TiledWebMapServer( URL serverURL, String caps_xml, boolean checkForUpdate) {        
        this(serverURL);
        this.getCaps_xml = caps_xml;
    	
    	// build a capabilities object from the given xml
    	WMSCCapabilities capabilities = null;
    	try {
	        final InputStream is = new ByteArrayInputStream(caps_xml.getBytes());
	        WMSCCapabilitiesResponse response;
	        
	        HTTPResponse mock = new HTTPResponse(){
                
                @Override
                public InputStream getResponseStream() throws IOException {
                    return is;
                }
                
                @Override
                public String getResponseHeader( String header ) {
                    return null;
                }
                
                @Override
                public String getContentType() {
                    return "text/xml"; //$NON-NLS-1$
                }
                
                @Override
                public void dispose() {
                    try {
                        is.close();
                    } catch (Exception e) {
                        // ignore.
                    }
                }
            };
	        
	        response = new WMSCCapabilitiesResponse(mock);
	        capabilities = (WMSCCapabilities) response.getCapabilities(); 
    	} catch (Exception e) {
    		log("Restore from cached capabilities failed", e);  //$NON-NLS-1$
    	}
    	
    	// try getting a new capabilities and see if its updatesequence is higher
        if (checkForUpdate) {
        	WMSCCapabilities newCaps;
            try {
            	newCaps = readCapabilities();                
                if (capabilities == null){
                    capabilities = newCaps;
                }else if (newCaps == null){
                    //cannot read a new capabilities; so lets use the cached one
                    this.getCaps_xml = caps_xml;
                }else{
                    //compare update sequence values
                    Double newUpdateSeq = newCaps.getUpdateSequence() == null ? null : Double.parseDouble(newCaps.getUpdateSequence());
                    Double capUpdateSeq = capabilities.getUpdateSequence() == null ? null :Double.parseDouble(capabilities.getUpdateSequence());
                    if (newUpdateSeq != null && capUpdateSeq != null ) {
                        if (newUpdateSeq > capUpdateSeq) {
                            capabilities = newCaps;
                        } else {
                            // xml would have been reset when reading caps, so set them back
                            this.getCaps_xml = caps_xml;
                        }
                    }else{
                        //at this point one of the update sequence numbers is null
                        //so lets just take the newest capabilities
                        capabilities = newCaps;
                    }
                }
            } catch (Exception ex) {
                // TODO: Do something with this error
                ex.printStackTrace();
            }        	
        }
        
        this.capabilities = capabilities;
    }

    /**
     * Get the getCapabilities document. If there was an error parsing it
     * during creation, it will return null (and it should have thrown an
     * exception during creation).
     * 
     * @return a WMSCCapabilities object, representing the Capabilities of the server
     * @throws IOException if we could not connect
     */
    public WMSCCapabilities getCapabilities() throws IOException {
        if (capabilities == null && couldNotConnect == null) {
            try {
                capabilities = readCapabilities();
            } catch (Exception ex) {
                couldNotConnect = ex;
            }
        }
        if( couldNotConnect != null ){
            if( couldNotConnect instanceof IOException){
                throw (IOException) couldNotConnect;
            }
            else {
                throw (IOException)
                    new IOException("Could not connect to " + internalServiceURL()).initCause(couldNotConnect); //$NON-NLS-1$
            }
        }
        return capabilities;
    }
    private static void log( String msg, Throwable t ){
        if( WmsPlugin.getDefault() == null ){
            System.out.print( msg );
            if( t != null ){
                t.printStackTrace();
            }
            else {
                System.out.println();
            }
        }
        else {
            WmsPlugin.log(msg, t);
        }
    }
    /**
     * Makes a getCapabilities request and parses the response into a WMSCCapabilities 
     * object.  Also stores the resulting getcaps xml.
     *
     * @return a WMSCCapabilities object
     * @throws ServiceException
     * @throws IOException
     */
    private WMSCCapabilities readCapabilities() throws Exception {
        String me = internalServiceURL();

        URL serverURL = new URL(me);
        
        //create a request
        CapabilitiesRequest r = new CapabilitiesRequest(serverURL);
        log("WMSC GetCapabilities: " + r.getFinalURL(), null);  //$NON-NLS-1$
        //issue the request
        WMSCCapabilitiesResponse cr;
        cr = (WMSCCapabilitiesResponse) issueRequest(r);
        
        // store the getcaps response xml
        if (cr != null) {
            getCaps_xml = cr.getCapabilitiesXml();
        }

        //return the parsed document
        return (WMSCCapabilities) cr.getCapabilities();
    }
    
    /**
     * Produce an initial "internal" URL out of the service information.
     *
     * @return
     */
    private String internalServiceURL() {
        //String me = service.getProtocol() + "://" + service.getHost() + ":" + service.getPort() //$NON-NLS-1$ //$NON-NLS-2$
        //    + "" + service.getPath(); //$NON-NLS-1$
        
        StringBuffer me = new StringBuffer();
        me.append( service.getProtocol() );
        me.append( "://" ); //$NON-NLS-1$
        me.append( service.getHost() );
        if( service.getPort() != -1 ){
            me.append( ":" );  //$NON-NLS-1$
            me.append( service.getPort() ); 
        }
        me.append( service.getPath() );
        
        return me.toString();
    }   
    
    /**
     * Get the getCapabilities xml string. If there was an error parsing it
     * during creation, it will return null (and it should have thrown an
     * exception during creation).
     * 
     * @return a String of xml, representing the Capabilities of the server
     */
    public String getCapabilitiesXml() throws IOException {
        if (getCaps_xml == null) {
            getCapabilities();
        }
        return getCaps_xml;
    }    

    /**
     * 
     * A capabilities request for a WMSC getCapabilities Request
     *
     * @author Emily Gouge (Refractions Research, Inc)
     * @since 1.1.0
     */
    static class CapabilitiesRequest extends AbstractGetCapabilitiesRequest {

        public CapabilitiesRequest( URL serverURL ) {
            super(serverURL);
        }

        @Override
        protected void initService() {
            setProperty(REQUEST, "GetCapabilities"); //$NON-NLS-1$
            setProperty(SERVICE, "WMS"); //$NON-NLS-1$;
            setProperty("TILED","true");
        }

        @Override
        protected void initVersion() {
            // not used?
        }

        public Response createResponse( HTTPResponse response )
                throws ServiceException, IOException {
            return new WMSCCapabilitiesResponse(response);
        }
    }

    /**
     * Issues a request to the server and returns that server's response. It asks the server to send
     * the response gzipped to provide a faster transfer time.
     * 
     * @param request the request to be issued
     * @return a response from the server, which is created according to the specific Request
     * @throws IOException if there was a problem communicating with the server
     * @throws ServiceException if the server responds with an exception or returns bad content
     */
    public Response issueRequest( Request request ) throws IOException, ServiceException {
        URL finalURL = request.getFinalURL();
        if( finalURL.getHost() == null ){
            //System.out.prinln("Poor WMS-C configuration - no host provided by "+ finalURL );
            throw new NullPointerException("No host provided by "+finalURL ); //$NON-NLS-1$
        }

        final HTTPResponse httpResponse;

        if (request.requiresPost()) {

            final String postContentType = request.getPostContentType();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            request.performPostOutput(out);
            InputStream in = new ByteArrayInputStream(out.toByteArray());

            try {
                httpResponse = httpClient.post(finalURL, in, postContentType);
            } finally {
                in.close();
            }
        } else {
            httpResponse = httpClient.get(finalURL);
        }

        final Response response = request.createResponse(httpResponse);
        return response;
    }

    /**
     * Build the base request URL for this server
     *
     * @return
     */
    public String buildBaseTileRequestURL() {
        return internalServiceURL() + "?"; //$NON-NLS-1$
    } 
    
    public URL getService() {
    	return service;
    }

}
