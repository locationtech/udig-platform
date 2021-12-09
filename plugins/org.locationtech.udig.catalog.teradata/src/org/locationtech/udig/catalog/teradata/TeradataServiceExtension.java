/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.teradata.TeradataDataStoreFactory;
import org.locationtech.udig.catalog.AbstractDataStoreServiceExtension;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.teradata.internal.Messages;

public class TeradataServiceExtension extends AbstractDataStoreServiceExtension
        implements ServiceExtension2 {

    public static final TeradataDialect DIALECT = new TeradataDialect();

    private static final TeradataDataStoreFactory FACTORY = new TeradataDataStoreFactory();

    /**
     * Constant to use with DBTYPE
     */
    public static final String TYPE = (String) DBTYPE.sample;

    @Override
    public IService createService(URL id, Map<String, Serializable> params) {
        try {
            if (getFactory() == null || !getFactory().isAvailable()) {
                return null; // factory not available
            }
            if (!getFactory().canProcess(params)) {
                return null; // the factory cannot use these parameters
            }
        } catch (Exception unexpected) {
            if (Platform.inDevelopmentMode()) {
                // this should never happen
                Activator.log("PostGISExtension canProcess errored out with: " + unexpected, //$NON-NLS-1$
                        unexpected);
            }
            return null; // the factory cannot really use these parameters
        }
        if (reasonForFailure(params) != null) {
            return null;
        }
        Map<String, Serializable> params2 = params;

        ensurePortIsInt(params2);

        try {
            URL finalID = DIALECT.toURL(params2);

            if (!params2.containsKey(TeradataDataStoreFactory.ESTIMATED_BOUNDS.key)) {
                params2.put(TeradataDataStoreFactory.ESTIMATED_BOUNDS.key, true);
            }
            if (!params2.containsKey(TeradataDataStoreFactory.APPLICATION.key)) {
                params2.put(TeradataDataStoreFactory.APPLICATION.key, "uDig"); //$NON-NLS-1$
            }
            params2.put(TeradataDataStoreFactory.TESSELLATION_TABLE.key,
                    TeradataDataStoreFactory.TESSELLATION_TABLE.sample.toString());

            if (!Activator.checkTeradataDrivers()) {
                return new TeradataServiceHolder(finalID, params2);
            }

            return new TeradataService(finalID, params2);
        } catch (MalformedURLException e) {
            Activator.log("Unable to construct proper service URL.", e); //$NON-NLS-1$
            return null;
        }
    }

    private void ensurePortIsInt(Map<String, Serializable> params) {
        if (params != null && params.containsKey(PORT.key)
                && params.get(PORT.key) instanceof String) {
            int val = Integer.valueOf((String) params.get(PORT.key));
            params.put(PORT.key, val);
        }
    }

    @Override
    public Map<String, Serializable> createParams(URL url) {
        if (!isTeradata(url)) {
            return null;
        }

        ParamInfo info = parseParamInfo(url);

        Map<String, Serializable> TeradataParams = new HashMap<>();
        TeradataParams.put(DBTYPE.key, (Serializable) DBTYPE.sample); // dbtype
        TeradataParams.put(USER.key, info.username); // user
        TeradataParams.put(PASSWD.key, info.password); // pass
        TeradataParams.put(HOST.key, info.host); // host
        TeradataParams.put(DATABASE.key, info.the_database); // database
        TeradataParams.put(PORT.key, info.the_port); // port
        TeradataParams.put(SCHEMA.key, info.the_schema); // database

        return TeradataParams;
    }

    /**
     * A couple quick checks on the URL
     */
    public static final boolean isTeradata(URL url) {
        if (url == null) {
            return false;
        }
        return url.getProtocol().toLowerCase().equals("teradata") //$NON-NLS-1$
                || url.getProtocol().toLowerCase().equals("teradata.jdbc") || //$NON-NLS-1$
                url.getProtocol().toLowerCase().equals("jdbc.teradata"); //$NON-NLS-1$
    }

    @Override
    protected TeradataDataStoreFactory getDataStoreFactory() {
        return FACTORY;
    }

    @Override
    public String reasonForFailure(URL url) {
        if (!isTeradata(url))
            return Messages.TeradataServiceExtension_badURL;
        return reasonForFailure(createParams(url));
    }

    public static TeradataDataStoreFactory getFactory() {
        return FACTORY;
    }

}
