/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.core.internal.CorePlugin;

public class OldCatalogRef {
    protected Layer layer;

    protected static final String ID_SEPERATOR = "-ID_SEP-"; //$NON-NLS-1$

    protected static final String CONNECTION_PARAM = "-ENTRY_SEP-"; //$NON-NLS-1$

    protected static final String KEY_VALUE_SEPERATOR = "-KEY_VALUE_SEP-"; //$NON-NLS-1$

    protected static final String ENCODING = ProjectPlugin.Implementation.ENCODING;

    protected static final String SERVICE_TAG = "-SERVICE_TAG-:"; //$NON-NLS-1$

    /**
     * @uml.property name="connectionParams"
     * @uml.associationEnd qualifier="key:java.lang.Object java.util.Map<String,Serializable>"
     */
    protected Map<URL, Map<String, Serializable>> connectionParams = Collections.synchronizedMap(new HashMap<URL, Map<String, Serializable>>());

    /**
     * Construct <code>LayerRef</code>.
     *
     * @param layer
     */
    public OldCatalogRef( Layer layer ) {
        this.layer = layer;
    }

    /**
     * Construct <code>LayerRef</code>.
     */
    public OldCatalogRef() {
        // do nothing
    }
    /**
     * @param string
     */
    public void parseResourceParameters( String string ) {

        String[] resources = string.split(SERVICE_TAG);
        for( int i = 0; i < resources.length; i++ ) {
            decodeConnectionParams(resources[i]);
        }

    }

    /**
     * @return A list of services identified by the string encodedService
     */
    protected void decodeConnectionParams( String encodedService ) {
        String[] tmp = encodedService.split(ID_SEPERATOR);
        String id = tmp[0];

        String[] entries = tmp[1].split(CONNECTION_PARAM);
        Map<String, Serializable> map = decodeConnectionParams(entries);
        URL url = decodeServiceURL(id, map);
        connectionParams.put(url, map);
    }

    /**
     * @return a map of the connection parameters obtained from parsing the encodedParams field
     */
    private Map<String, Serializable> decodeConnectionParams( String[] encodedParams ) {
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        for( int j = 0; j < encodedParams.length; j++ ) {
            String[] entry = encodedParams[j].split(KEY_VALUE_SEPERATOR);
            if (entry[0].equalsIgnoreCase("URL")) //$NON-NLS-1$
                try {
                    entry[1] = decodeURL(entry[1]);
                } catch (UnsupportedEncodingException e) {
                    // do nothing
                }
            if (entry.length == 2)
                map.put(entry[0], entry[1]);
        }
        return map;
    }

    private URL decodeServiceURL( String id, Map<String, Serializable> map ) {
        URL url = null;
        try {
            url = new URL(null, decodeURL(id), CorePlugin.RELAXED_HANDLER);
        } catch (Exception e) {
            try {
                url = new URL(decodeURL((String) map.get("url"))); //$NON-NLS-1$
            } catch (Exception e2) {
                e.printStackTrace();
                return null;
            }
        }
        return url;
    }

    /**
     * @return a url from the string url
     * @throws UnsupportedEncodingException
     */
    private String decodeURL( String url ) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, ENCODING);
    }

}
