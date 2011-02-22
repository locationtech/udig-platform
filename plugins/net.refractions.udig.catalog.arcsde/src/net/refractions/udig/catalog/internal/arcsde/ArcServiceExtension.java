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
package net.refractions.udig.catalog.internal.arcsde;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.AbstractDataStoreServiceExtension;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.arcsde.internal.Messages;

import org.geotools.data.arcsde.ArcSDEDataStoreFactory;

/**
 * Arc SDE Service Extension Implementation.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ArcServiceExtension extends AbstractDataStoreServiceExtension implements ServiceExtension {

    public IService createService( URL id, Map<String, Serializable> params ) {
        if (params != null && params.containsKey(getFactory().getParametersInfo()[3].key)
                && params.get(getFactory().getParametersInfo()[3].key) instanceof String) {
            String val = (String) params.get(getFactory().getParametersInfo()[3].key);
            params.remove(val);
            params.put(getFactory().getParametersInfo()[3].key, new Integer(val));
        }

        if (!getFactory().canProcess(params))
            return null;
        if (id == null) {
            String host = (String) params.get(getFactory().getParametersInfo()[2].key);
            String port = params.get(getFactory().getParametersInfo()[3].key).toString();
            String db = (String) params.get(getFactory().getParametersInfo()[4].key);
            try {
                return new ArcServiceImpl(new URL(
                        "http://" + host + ".arcsde.jdbc:" + (port) + "/" + db), params); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } catch (MalformedURLException e) {
                // log this?
                e.printStackTrace();
                return null;
            }
        }
        return new ArcServiceImpl(id, params);
    }

    public Map<String, Serializable> createParams( URL url ) {

        if( !isArcSDE(url))
            return null;

        ParamInfo info = parseParamInfo(url);

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(getFactory().getParametersInfo()[1].key, "arcsde"); // dbtype //$NON-NLS-1$
        params.put(getFactory().getParametersInfo()[2].key, info.host); // host
        params.put(getFactory().getParametersInfo()[3].key, info.the_port); // port
        params.put(getFactory().getParametersInfo()[4].key, info.the_database); // database
        params.put(getFactory().getParametersInfo()[5].key, info.username); // user
        params.put(getFactory().getParametersInfo()[6].key, info.password); // pass

        if (getFactory().canProcess(params)) {
            return params;
        }
        return null;
    }

    /** A couple quick checks on the url */
    public static final boolean isArcSDE( URL url ) {
        if( url==null )
            return false;
        return url.getProtocol().toLowerCase().equals("arcsde"); //$NON-NLS-1$
    }

    private static ArcSDEDataStoreFactory factory = null;

    public static ArcSDEDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new ArcSDEDataStoreFactory();
        }
        return factory;
    }

    public String reasonForFailure( URL url ) {
        if( url==null )
            return Messages.ArcServiceExtension_urlNull;
        if( !isArcSDE(url) )
            return Messages.ArcServiceExtension_notSDEURL;
        return reasonForFailure(createParams(url));
    }
}
