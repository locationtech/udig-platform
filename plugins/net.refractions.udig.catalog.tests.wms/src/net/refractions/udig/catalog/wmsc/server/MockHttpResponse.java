/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010 Open Source Geospatial Foundation
 * (C) 2011 Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.wmsc.server;

import java.io.IOException;
import java.io.InputStream;

import org.geotools.data.ows.HTTPResponse;

/**
 * Used to mock WMS tests by providing a "ready to use" response with both content to return; and
 * mime type information.
 */
public class MockHttpResponse implements HTTPResponse{

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