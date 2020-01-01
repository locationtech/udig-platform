/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.arcsde;

import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.DBTYPE_PARAM;
import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.INSTANCE_PARAM;
import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.PASSWORD_PARAM;
import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.PORT_PARAM;
import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.SERVER_PARAM;
import static org.geotools.arcsde.data.ArcSDEDataStoreFactory.USER_PARAM;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.AbstractDataStoreServiceExtension;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension;
import org.locationtech.udig.catalog.arcsde.internal.Messages;

import org.geotools.arcsde.data.ArcSDEDataStoreFactory;
import org.geotools.data.DataStoreFactorySpi;

/**
 * Arc SDE Service Extension Implementation.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ArcServiceExtension extends AbstractDataStoreServiceExtension
        implements
            ServiceExtension {

    public IService createService( URL id, Map<String, Serializable> params ) {
        final ArcSDEDataStoreFactory factory = getFactory();

        if (params != null && params.containsKey(PORT_PARAM.key)
                && params.get(PORT_PARAM.key) instanceof String) {
            String val = (String) params.get(PORT_PARAM.key);
            params.put(PORT_PARAM.key, Integer.valueOf(val));
        }

        if (!factory.canProcess(params))
            return null;
        if (id == null) {
            String host = (String) params.get(SERVER_PARAM.key);
            String port = params.get(PORT_PARAM.key).toString();
            String db = (String) params.get(INSTANCE_PARAM.key);
            if (null == db) {
                db = "";
            }
            try {
                URL serviceId = new URL("http://" + host + ":" + (port) + "/arcsde/" + db);
                return new ArcServiceImpl(serviceId, params); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } catch (MalformedURLException e) {
                // log this?
                e.printStackTrace();
                return null;
            }
        }
        return new ArcServiceImpl(id, params);
    }

    public Map<String, Serializable> createParams( URL url ) {

        if (!isArcSDE(url))
            return null;

        ParamInfo info = parseParamInfo(url);

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(DBTYPE_PARAM.key, (Serializable) DBTYPE_PARAM.sample); // dbtype //$NON-NLS-1$
        params.put(SERVER_PARAM.key, info.host); // host
        params.put(PORT_PARAM.key, info.the_port); // port
        params.put(INSTANCE_PARAM.key, info.the_database); // database
        params.put(USER_PARAM.key, info.username); // user
        params.put(PASSWORD_PARAM.key, info.password); // pass

        if (getFactory().canProcess(params)) {
            return params;
        }
        return null;
    }

    /** A couple quick checks on the url */
    public static final boolean isArcSDE( URL url ) {
        if (url == null)
            return false;
        return url.getProtocol().toLowerCase().equals("arcsde"); //$NON-NLS-1$
    }

    private static ArcSDEDataStoreFactory factory = null;

    /**
     * Factory describing ArcSDE connection parameters
     * 
     * @return factory describing ArcSDE connection parameters
     */
    protected static ArcSDEDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new ArcSDEDataStoreFactory();
        }
        return factory;
    }

    @Override
    protected String doOtherChecks( Map<String, Serializable> params ) {
        return null;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getFactory();
    }

    public String reasonForFailure( URL url ) {
        if (url == null)
            return Messages.ArcServiceExtension_urlNull;
        if (!isArcSDE(url))
            return Messages.ArcServiceExtension_notSDEURL;
        return reasonForFailure(createParams(url));
    }
}
