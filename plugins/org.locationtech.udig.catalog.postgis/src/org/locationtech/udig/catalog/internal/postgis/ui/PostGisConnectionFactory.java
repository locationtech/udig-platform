/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2009, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.postgis.ui;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.DBTYPE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;
import static org.locationtech.udig.catalog.PostgisServiceExtension2.DIALECT;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.PostgisGeoResource2;
import org.locationtech.udig.catalog.PostgisSchemaFolder;
import org.locationtech.udig.catalog.PostgisService2;
import org.locationtech.udig.catalog.PostgisServiceExtension2;
import org.locationtech.udig.catalog.internal.postgis.PostgisPlugin;
import org.locationtech.udig.catalog.ui.UDIGConnectionFactory;

public class PostGisConnectionFactory extends UDIGConnectionFactory {

    @Override
    public boolean canProcess(Object context) {
        return toCapabilitiesURL(context) != null;
    }

    @Override
    public Map<String, Serializable> createConnectionParameters(Object context) {
        if (context instanceof PostgisService2) {
            PostgisService2 postgis = (PostgisService2) context;
            return postgis.getConnectionParams();
        }
        URL url = toCapabilitiesURL(context);
        if (url == null) {
            // so we are not sure it is a PostGIS URL
            // lets guess
            ID sample = ID.cast(context);
            url = sample != null ? sample.toURL() : null;
        }
        if (url != null && PostgisServiceExtension2.isPostGIS(url)) {
            // well we have a URL - lets try it!
            List<IResolve> list = CatalogPlugin.getDefault().getLocalCatalog().find(url, null);
            for (IResolve resolve : list) {
                if (resolve instanceof PostgisService2) {
                    // got a hit!
                    PostgisService2 postgisService = (PostgisService2) context;
                    return postgisService.getConnectionParams();
                } else if (resolve instanceof PostgisSchemaFolder) {
                    PostgisSchemaFolder postgisFolder = (PostgisSchemaFolder) resolve;
                    return postgisFolder.getService(new NullProgressMonitor())
                            .getConnectionParams();
                } else if (resolve instanceof PostgisGeoResource2) {
                    PostgisGeoResource2 layer = (PostgisGeoResource2) resolve;
                    PostgisService2 postgis;
                    try {
                        postgis = (PostgisService2) layer.parent(null);
                        return postgis.getConnectionParams();
                    } catch (IOException e) {
                        toCapabilitiesURL(layer.getIdentifier());
                    }
                }
            }
            return createParams(url);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public URL createConnectionURL(Object context) {
        if (context instanceof URL) {
            return (URL) context;
        }
        if (context instanceof Map) {
            @SuppressWarnings("rawtypes")
            Map params = (Map) context;

            try {
                return DIALECT.toURL(params);
            } catch (MalformedURLException e) {
                return null;
            }

        }
        if (context instanceof String) {
            return toCapabilitiesURL((String) context);
        }
        return null;
    }

    /**
     * Convert "data" to a PostGIS URL
     * <p>
     * Candidates for conversion are:
     * <ul>
     * <li>URL - from browser DnD
     * <li>PostgisService2 (the uDig class) - from catalog DnD
     * <li>IService - from search DnD
     * </ul>
     * </p>
     * <p>
     * No external processing should be required here, it is enough to guess and let the
     * ServiceFactory try a real connect.
     * </p>
     *
     * @param data IService, URL, or something else
     * @return URL considered a possibility for a PostGIS connection, or null
     */
    protected URL toCapabilitiesURL(Object data) {
        if (data instanceof IResolve) {
            return toCapabilitiesURL((IResolve) data);
        } else if (data instanceof URL) {
            return toCapabilitiesURL((URL) data);
        } else if (data instanceof String) {
            return toCapabilitiesURL((String) data);
        } else if (ID.cast(data) != null && ID.cast(data).toURL() != null) {
            return toCapabilitiesURL(ID.cast(data).toURL());
        } else {
            return null; // no idea what this should be
        }
    }

    protected URL toCapabilitiesURL(IResolve resolve) {
        if (resolve instanceof IService) {
            return toCapabilitiesURL((IService) resolve);
        }
        return toCapabilitiesURL(resolve.getIdentifier());
    }

    protected URL toCapabilitiesURL(IService resolve) {
        if (resolve instanceof PostgisService2) {
            return toCapabilitiesURL((PostgisService2) resolve);
        }
        return toCapabilitiesURL(resolve.getIdentifier());
    }

    /**
     * No further QA checks needed - we know this one works
     */
    protected URL toCapabilitiesURL(PostgisService2 postgis) {
        return postgis.getIdentifier();
    }

    /**
     * Quick sanity check to see if URL is a PostGIS URL
     */
    protected URL toCapabilitiesURL(URL url) {
        if (url == null)
            return null;

        String protocol = url.getProtocol() != null ? url.getProtocol().toLowerCase() : null;

        if (!"http".equals(protocol) //$NON-NLS-1$
                && !"https".equals(protocol) && !DIALECT.urlPrefix.equals(protocol)) { //$NON-NLS-1$
            return null;
        }
        if (url.toExternalForm().indexOf(DIALECT.urlPrefix) != -1) {
            return url;
        }
        return null;
    }

    /**
     * Quick sanity check to see if URL is a PostGIS URL String
     */
    protected URL toCapabilitiesURL(String string) {
        if (string == null)
            return null;

        if (!string.contains(DIALECT.urlPrefix) && !string.contains("jdbc.postgis") //$NON-NLS-1$
                && !string.contains("postgis")) { //$NON-NLS-1$
            return null;
        }
        // jdbc.postgresql://username:password@host:port/database
        int startindex = string.indexOf("//") + 2; //$NON-NLS-1$
        int usernameEnd = string.indexOf(":", startindex); //$NON-NLS-1$
        int passwordEnd = string.indexOf("@", usernameEnd); //$NON-NLS-1$
        int hostEnd = string.indexOf(":", passwordEnd); //$NON-NLS-1$
        int portEnd = string.indexOf("/", hostEnd); //$NON-NLS-1$
        int databaseEnd = string.indexOf("/", portEnd + 1); //$NON-NLS-1$

        // int databaseEnd = string.indexOf(" ", databaseStart);
        String the_host = string.substring(passwordEnd + 1, hostEnd);
        String the_username = string.substring(startindex, usernameEnd);
        String the_port;
        String the_database;

        if (portEnd < 1) {
            the_port = string.substring(hostEnd + 1);
            the_database = ""; //$NON-NLS-1$
        } else {
            the_port = string.substring(hostEnd + 1, portEnd);
            the_database = string.substring(portEnd + 1, databaseEnd);
        }
        Integer intPort;
        if (!the_port.equalsIgnoreCase("")) { //$NON-NLS-1$
            intPort = Integer.valueOf(the_port);
        } else {
            intPort = Integer.valueOf(5432);
        }

        // URL(String protocol, String host, int port, String file)
        URL url = null;
        try {
            url = DIALECT.toURL(the_username, the_host, intPort, the_database);

        } catch (MalformedURLException e) {
            // TODO Catch e
            PostgisPlugin.log("bad url", e); //$NON-NLS-1$
        }
        return url;
    }

    /**
     * 'Create' params given the provided URL, no magic occurs
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Serializable> createParams(URL url) {
        PostgisServiceExtension2 serviceFactory = new PostgisServiceExtension2();
        @SuppressWarnings("rawtypes")
        Map params = serviceFactory.createParams(url);
        if (params != null)
            return params;

        Map<String, Serializable> params2 = new HashMap<>();

        params2.put(DBTYPE.key, (Serializable) DBTYPE.sample);
        params2.put(HOST.key, url.getHost());
        String dbport = ((Integer) url.getPort()).toString();
        try {
            params2.put(PORT.key, Integer.valueOf(dbport));
        } catch (NumberFormatException e) {
            params2.put(PORT.key, Integer.valueOf(5432));
        }

        String the_database = url.getPath() == null ? "" : url.getPath(); //$NON-NLS-1$
        params2.put(DATABASE.key, the_database); // database
        String userInfo = url.getUserInfo() == null ? "" : url.getUserInfo(); //$NON-NLS-1$
        params2.put(USER.key, userInfo); // user
        params2.put(PASSWD.key, ""); // pass //$NON-NLS-1$

        return params2;
    }

}
