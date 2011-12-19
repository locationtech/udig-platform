/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010 Open Source Geospatial Foundation
 * (C) 2011 Refractions Research Inc.
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