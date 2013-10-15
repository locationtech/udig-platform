/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.wmsc.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.data.ows.Capabilities;
import org.geotools.data.ows.GetCapabilitiesResponse;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.ows.ServiceException;
import org.geotools.xml.DocumentFactory;
import org.geotools.xml.handlers.DocumentHandler;
import org.xml.sax.SAXException;

/**
 * 
 * This class takes the WMSC getCapabilities request response and parses out
 * the capabilities document.
 *<p>
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 * </p>
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc)
 * @since 1.1.0
 */
public class WMSCCapabilitiesResponse extends GetCapabilitiesResponse {
	
	private String getCaps_xml;

    /**
     * Creates a new response for a given content type and input.
     * @param contentType
     * @param inputStream
     * @throws ServiceException
     * @throws IOException
     */
    public WMSCCapabilitiesResponse( HTTPResponse response )
            throws ServiceException, IOException {
        super(response);
        
        // first store the getcaps as a string so we can persist it, then create a 
        // new inpuststream from it to convert into a getCaps object.
        try{
        getCaps_xml = convertStreamToString(response.getResponseStream());
        }finally{
            response.dispose();
        }
        InputStream inputStream = new ByteArrayInputStream(getCaps_xml.getBytes());
        
        try {
            Map<String, Object> hints = new HashMap<String, Object>();
            hints.put(DocumentHandler.DEFAULT_NAMESPACE_HINT_KEY, WMSCSchema.getInstance());
            hints.put(DocumentFactory.VALIDATION_HINT, Boolean.FALSE);
    
            Object object;
            try {
                object = DocumentFactory.getInstance(inputStream, hints, Level.WARNING);
            } catch (SAXException e) {
                throw (ServiceException) new ServiceException("Error while parsing XML.").initCause(e); //$NON-NLS-1$
            }
            
            if (object instanceof ServiceException) {
                throw (ServiceException) object;
            }
            
            this.capabilities = (Capabilities)object;
        } finally {
            inputStream.close();
        }
    }
    
    static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n"); //$NON-NLS-1$
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }

	public String getCapabilitiesXml() {
		return getCaps_xml;
	}
}
