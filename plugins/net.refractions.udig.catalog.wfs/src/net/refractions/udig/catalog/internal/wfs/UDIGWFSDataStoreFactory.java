/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.wfs;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.wfs.internal.Messages;

import org.geotools.data.DataStore;
import org.geotools.data.ows.WFSCapabilities;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.xml.sax.SAXException;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class UDIGWFSDataStoreFactory extends WFSDataStoreFactory {



    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException
     *
     * @see org.geotools.data.DataStoreFactorySpi#createNewDataStore(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public DataStore createNewDataStore(Map arg0) throws IOException {
        Map<String,Serializable> params = (Map<String,Serializable>)arg0;
        URL host = (URL)URL.lookUp(params);

        Boolean protocol = (Boolean)PROTOCOL.lookUp(params);

        String user;
        String pass;
        user = pass = null;

        int timeout = 3000;
        int buffer = 10;
        boolean tryGZIP=true;

        if (params.containsKey(TRY_GZIP.key)) {
            Boolean b = (Boolean) TRY_GZIP.lookUp(params);
            if(b!=null)
                tryGZIP = b.booleanValue();
        }
        if (params.containsKey(TIMEOUT.key)) {
            Integer i = (Integer) TIMEOUT.lookUp(params);
            if(i!=null)
                timeout = i.intValue();
        }

        if (params.containsKey(BUFFER_SIZE.key)) {
            Integer i = (Integer) BUFFER_SIZE.lookUp(params);
            if(i!=null)
                buffer = i.intValue();
        }

        if (params.containsKey(USERNAME.key)) {
            user = (String) USERNAME.lookUp(params);
        }

        if (params.containsKey(PASSWORD.key)) {
            pass = (String) PASSWORD.lookUp(params);
        }

        if (((user == null) && (pass != null))
                || ((pass == null) && (user != null))) {
            throw new IOException(
                Messages.UDIGWFSDataStoreFactory_error_usernameAndPassword);
        }

        DataStore ds = null;

        try {
            ds = new UDIGWFSDataStore(host, protocol, user, pass, timeout, buffer, tryGZIP);
            cache.put(params, ds);
        } catch (SAXException e) {
            WfsPlugin.log( "Encountered SAXException" , e); //$NON-NLS-1$
            throw (IOException) new IOException().initCause(e);
        }

        return ds;
    }

    public static class UDIGWFSDataStore extends WFSDataStore{

        /**
         * Construct <code>UDIGWFSDataStore</code>.
         *
         * @param arg0
         * @param arg1
         * @param arg2
         * @param arg3
         * @param arg4
         * @param arg5
         * @param arg6
         * @throws SAXException
         * @throws IOException
         */
        UDIGWFSDataStore(URL host, Boolean protocol, String username,
                String password, int timeout, int buffer, boolean tryGZIP) throws SAXException, IOException {
            super(host, protocol, username, password, timeout, buffer, tryGZIP);
        }

        public WFSCapabilities getCapabilities(){
            return capabilities;
        }

        protected static URL createGetCapabilitiesRequest(URL host) {
            return WFSDataStore.createGetCapabilitiesRequest(host);
        }
    }
}
