/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.locationtech.udig.catalog.internal.wms.WmsPlugin;

import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.data.ows.AbstractGetCapabilitiesRequest;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.Response;
import org.geotools.data.ows.Specification;
import org.geotools.ows.wms.WMS1_1_1;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.ServiceException;

/**
 * TiledWebMapServer is a class representing a WMSC. It is used to access the Capabilities document
 * and perform requests. See http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation
 * 
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc)
 * @since 1.1.0
 */
public class TiledWebMapServer extends AbstractOpenWebService<WMSCCapabilities, TileSet> {

    /** Capabilities document */
    private WMSCCapabilities capabilities = null;

    /** Error connecting */
    private Exception couldNotConnect;

    /**
     * Raw caps XML
     */
    private String getCaps_xml;

    /**
     * Creates a new service with the given url
     * 
     * @param serverURL
     * @throws IOException
     * @throws ServiceException
     */
    public TiledWebMapServer( URL serverURL ) throws ServiceException, IOException {
        super(serverURL);
    }

    /**
     * Creates a new service with the given capabilities xml. If checkForUpdate is true, then it
     * also tries to request a new capabilities document to see if there is any update it needs (if
     * it can't connect it just continues with the given caps).
     * 
     * @param serverURL
     * @param caps_xml
     * @param checkForUpdate
     * @throws IOException
     * @throws ServiceException
     */
    public TiledWebMapServer( URL serverURL, String caps_xml, boolean checkForUpdate )
            throws ServiceException, IOException {
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

                @Override
                public String getResponseCharset() {
                    
                    return "UTF-8";
                }
            };

            response = new WMSCCapabilitiesResponse(mock);
            capabilities = (WMSCCapabilities) response.getCapabilities();
        } catch (Exception e) {
            log("Restore from cached capabilities failed", e); //$NON-NLS-1$
        }

        // try getting a new capabilities and see if its updatesequence is higher
        if (checkForUpdate) {
            WMSCCapabilities newCaps;
            try {
                newCaps = readCapabilities();
                if (capabilities == null) {
                    capabilities = newCaps;
                } else if (newCaps == null) {
                    // cannot read a new capabilities; so lets use the cached one
                    this.getCaps_xml = caps_xml;
                } else {
                    // compare update sequence values
                    Double newUpdateSeq = newCaps.getUpdateSequence() == null ? null : Double
                            .parseDouble(newCaps.getUpdateSequence());
                    Double capUpdateSeq = capabilities.getUpdateSequence() == null ? null : Double
                            .parseDouble(capabilities.getUpdateSequence());
                    if (newUpdateSeq != null && capUpdateSeq != null) {
                        if (newUpdateSeq > capUpdateSeq) {
                            capabilities = newCaps;
                        } else {
                            // xml would have been reset when reading caps, so set them back
                            this.getCaps_xml = caps_xml;
                        }
                    } else {
                        // at this point one of the update sequence numbers is null
                        // so lets just take the newest capabilities
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
     * Get the getCapabilities document. If there was an error parsing it during creation, it will
     * return null (and it should have thrown an exception during creation).
     * 
     * @return a WMSCCapabilities object, representing the Capabilities of the server
     * @throws IOException if we could not connect
     */
    @Override
    public WMSCCapabilities getCapabilities() {
        if (capabilities == null && couldNotConnect == null) {
            try {
                capabilities = readCapabilities();
            } catch (Exception ex) {
                couldNotConnect = ex;
            }
        }
        if (couldNotConnect != null) {
            log("Could not connect to " + getInfo().getSource(), couldNotConnect); //$NON-NLS-1$
        }
        return capabilities;
    }
    private static void log( String msg, Throwable t ) {
        if (WmsPlugin.getDefault() == null) {
            System.out.print(msg);
            if (t != null) {
                t.printStackTrace();
            } else {
                System.out.println();
            }
        } else {
            WmsPlugin.log(msg, t);
        }
    }

    /**
     * Makes a getCapabilities request and parses the response into a WMSCCapabilities object. Also
     * stores the resulting getcaps xml.
     * 
     * @return a WMSCCapabilities object
     * @throws ServiceException
     * @throws IOException
     */
    private WMSCCapabilities readCapabilities() throws Exception {
        // create a request
        CapabilitiesRequest r = new CapabilitiesRequest(serverURL);
        log("WMSC GetCapabilities: " + r.getFinalURL(), null); //$NON-NLS-1$
        // issue the request
        WMSCCapabilitiesResponse cr;
        cr = (WMSCCapabilitiesResponse) issueRequest(r);

        // store the getcaps response xml
        if (cr != null) {
            getCaps_xml = cr.getCapabilitiesXml();
        }

        // return the parsed document
        return (WMSCCapabilities) cr.getCapabilities();
    }

    /**
     * Get the getCapabilities xml string. If there was an error parsing it during creation, it will
     * return null (and it should have thrown an exception during creation).
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
            setProperty("TILED", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        protected void initVersion() {
            // not used?
        }

        public Response createResponse( HTTPResponse response ) throws ServiceException,
                IOException {
            return new WMSCCapabilitiesResponse(response);
        }
    }

    public URL getService() {
        return serverURL;
    }

    @Override
    protected ServiceInfo createInfo() {
        return new WMSCInfo();
    }

    @Override
    protected ResourceInfo createInfo( TileSet arg0 ) {
        return null;
    }

    /**
     * Sets up the specifications/versions that this server is capable of communicating with.
     */
    protected void setupSpecifications() {
        specs = new Specification[1];
        specs[0] = new WMS1_1_1();
    }

    protected class WMSCInfo implements ServiceInfo {

        private Set<String> keywords;
        private Icon icon;

        WMSCInfo() {
            keywords = new HashSet<String>();
            if (capabilities == null) {
                try {
                    getCapabilities();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (capabilities.getService() != null) {
                String array[] = capabilities.getService().getKeywordList();
                if (array != null) {
                    keywords.addAll(Arrays.asList(array));
                }
            }
            keywords.add("WMSC"); //$NON-NLS-1$
            keywords.add(serverURL.toString());

            URL globe2 = WebMapServer.class.getResource("Globe2.png"); //$NON-NLS-1$
            icon = new ImageIcon(globe2);
        }

        public String getDescription() {
            String description = null;
            if (capabilities != null && capabilities.getService() != null) {
                description = capabilities.getService().get_abstract();
            }
            if (description == null) {
                description = "Tiled Map Server " + serverURL; //$NON-NLS-1$
            }
            return description;
        }

        public Icon getIcon() {
            return icon;
        }

        public Set<String> getKeywords() {
            return keywords;
        }

        public URI getPublisher() {
            try {
                return capabilities.getService().getContactInformation().getContactInfo()
                        .getOnLineResource().getLinkage();
            } catch (NullPointerException publisherNotAvailable) {
            }
            try {
                return new URI(serverURL.getProtocol() + ":" + serverURL.getHost()); //$NON-NLS-1$
            } catch (URISyntaxException e) {
            }
            return null;
        }

        /**
         * We are a Web Map Service:
         * 
         * @return WMSSchema.NAMESPACE;
         */
        public URI getSchema() {
            return WMSCSchema.NAMESPACE;
        }

        /**
         * The source of this WMS is the capabilities document.
         * <p>
         * We make an effort here to look in the capabilities document provided for the unambiguous
         * capabilities URI. This covers the case where the capabilities document has been cached on
         * disk and we are restoring a WebMapServer instance.
         */
        public URI getSource() {
            try {
                URL source = getService();
                return source.toURI();
            } catch (NullPointerException huh) {
            } catch (URISyntaxException e) {
            }
            try {
                return serverURL.toURI();
            } catch (URISyntaxException e) {
                return null;
            }
        }

        public String getTitle() {
            if (capabilities != null && capabilities.getService() != null) {
                return capabilities.getService().getTitle();
            } else if (serverURL == null) {
                return "Unavailable"; //$NON-NLS-1$
            } else {
                return serverURL.toString();
            }
        }
    }

}
