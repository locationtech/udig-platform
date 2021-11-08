/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.oracle;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.locationtech.udig.catalog.AbstractDataStoreServiceExtension;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.oracle.internal.Messages;
import org.locationtech.udig.core.internal.CorePlugin;

/**
 * Oracle Service Extension implementation.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class OracleServiceExtension extends AbstractDataStoreServiceExtension
        implements ServiceExtension2 {

    private static OracleNGDataStoreFactory factory = null;

    /**
     * Create a OracleServiceImpl if the provided parameters are applicable.
     * <p>
     * Please note the OracleServiceImpl returned may not be able to connect; it depends on the
     * database actually being on, parameters being correct, etc...
     * </p>
     *
     * @see org.locationtech.udig.catalog.ServiceExtension#createService(java.net.URI,
     *      java.util.Map)
     * @param id
     * @param params
     * @return OracleServiceImpl or null if the params do not provide enough information to connect
     */
    @Override
    public OracleServiceImpl createService(URL id, Map<String, Serializable> params) {
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
                OraclePlugin.log(
                        "OracleServiceExtension canProcess errored out with: " + unexpected, //$NON-NLS-1$
                        unexpected);
            }
            return null; // the factory cannot really use these parameters
        }
        if (id == null) {
            String jdbc_url = getJDBCUrl(params);
            if (jdbc_url == null) {
                return null; // parameters are not sufficient
            }
            try {
                id = new URL(null, jdbc_url, CorePlugin.RELAXED_HANDLER);
            } catch (MalformedURLException e) {
                return null; // parameters are not sufficient
            }
        }
        return new OracleServiceImpl(id, params);
    }

    /**
     * This is a guess ...
     *
     * @see org.locationtech.udig.catalog.ServiceExtension#createParams(java.net.URL)
     * @param url
     * @return
     */
    @Override
    public Map<String, Serializable> createParams(URL url) {
        if (!isOracle(url)) {
            return null;
        }
        ParamInfo info = parseParamInfo(url);

        Map<String, Serializable> params = new HashMap<>();
        params.put(OracleNGDataStoreFactory.DBTYPE.key, "oracle"); // dbtype //$NON-NLS-1$
        params.put(OracleNGDataStoreFactory.HOST.key, info.host); // host
        params.put(OracleNGDataStoreFactory.PORT.key, info.the_port); // port
        params.put(OracleNGDataStoreFactory.USER.key, info.username); // user
        params.put(OracleNGDataStoreFactory.PASSWD.key, info.password); // pass
        params.put(OracleNGDataStoreFactory.DATABASE.key, info.the_database); // database

        return params;
    }

    /**
     * Holds onto the Oracle DataStoreFactorySPI for us.
     *
     * @return OracleNGDataStoreFactory instance
     */
    public synchronized static OracleNGDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new OracleNGDataStoreFactory();
            // TODO: look this up using DataAccessFinder so we don't get duplicate instances
            // (DB2ServiceExtension has the example)
        }
        return factory;
    }

    /** Create a "jdbc_url" from the provided parameters */
    static String getJDBCUrl(Map<String, Serializable> params) {
        final String JDBC_PATH = "jdbc:oracle:thin:@"; //$NON-NLS-1$
        try {
            String host = (String) OracleNGDataStoreFactory.HOST.lookUp(params);
            String db = (String) OracleNGDataStoreFactory.DATABASE.lookUp(params);
            int port = (Integer) OracleNGDataStoreFactory.PORT.lookUp(params);
            if (db.startsWith("(")) { //$NON-NLS-1$
                return JDBC_PATH + db;
            } else if (db.startsWith("/")) { //$NON-NLS-1$
                return JDBC_PATH + "//" + host + ":" + port + db; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                return JDBC_PATH + host + ":" + port + ":" + db; //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (IOException e) {
            return null; // not for us then
        }
    }

    /**
     * A couple quick checks on the URL This should perhaps do more, but I can't think of a good
     * test that will tell me without a doubt that the URL is an Oracle URL.
     */
    private static final boolean isOracle(URL url) {
        if (url == null)
            return false;
        return url.getProtocol().toLowerCase().equals("oracle") //$NON-NLS-1$
                || url.getProtocol().toLowerCase().equals("oracle.jdbc") || //$NON-NLS-1$
                url.getProtocol().toLowerCase().equals("jdbc.oracle"); //$NON-NLS-1$
    }

    @Override
    public String reasonForFailure(URL url) {
        if (!isOracle(url)) {
            return Messages.OracleServiceExtension_badUrl;
        }
        return reasonForFailure(createParams(url));
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getFactory();
    }
}
