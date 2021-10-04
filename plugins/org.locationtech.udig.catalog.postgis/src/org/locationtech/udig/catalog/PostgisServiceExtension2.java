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
package org.locationtech.udig.catalog;

import static java.text.MessageFormat.format;
import static org.geotools.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotools.data.postgis.PostgisNGDataStoreFactory.SCHEMA;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.DBTYPE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.locationtech.udig.catalog.internal.postgis.PostgisPlugin;
import org.locationtech.udig.catalog.internal.postgis.ui.PostgisServiceDialect;
import org.locationtech.udig.catalog.postgis.internal.Messages;
import org.locationtech.udig.core.Pair;

/**
 * PostGis ServiceExtension that has a hierarchy. It represents a Database and has folders within
 * it. One for each Schema that is known. The params object is the same as a normal Postgis except
 * that the schema parameter can be a list of comma separated string.
 *
 * @author Jesse Eichar, Refractions Research
 * @since 1.2
 */
public class PostgisServiceExtension2 extends AbstractDataStoreServiceExtension
        implements ServiceExtension2 {
    /** Constant to use with DBTYPE */
    public static final String TYPE = (String) DBTYPE.sample;

    /** Key used to test connection */
    private static final String IN_TESTING = "testing"; //$NON-NLS-1$

    /**
     * Common SQL functionality needed for the user interface
     */
    public static final PostgisServiceDialect DIALECT = new PostgisServiceDialect();

    @Override
    public PostgisService2 createService(URL id, Map<String, Serializable> params) {
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
                PostgisPlugin.log("PostGISExtension canProcess errored out with: " + unexpected, //$NON-NLS-1$
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
            Pair<Map<String, Serializable>, String> split = processParams(params2);
            if (split.getRight() != null) {
                return null;
            }

            return new PostgisService2(finalID, split.getLeft());
        } catch (MalformedURLException e) {
            PostgisPlugin.log("Unable to construct proper service URL.", e); //$NON-NLS-1$
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
        if (!isPostGIS(url)) {
            return null;
        }

        ParamInfo info = parseParamInfo(url);

        Map<String, Serializable> postGISParams = new HashMap<>();
        postGISParams.put(DBTYPE.key, (Serializable) DBTYPE.sample); // dbtype
        postGISParams.put(USER.key, info.username); // user
        postGISParams.put(PASSWD.key, info.password); // pass
        postGISParams.put(HOST.key, info.host); // host
        postGISParams.put(DATABASE.key, info.the_database); // database
        postGISParams.put(PORT.key, info.the_port); // port
        postGISParams.put(SCHEMA.key, info.the_schema); // database

        return postGISParams;
    }

    private static PostgisNGDataStoreFactory factory;

    public synchronized static PostgisNGDataStoreFactory getFactory() {
        if (factory == null) {
            factory = new PostgisNGDataStoreFactory();
            // TODO look up in factory SPI in order to avoid
            // duplicate instances
        }
        return factory;
    }

    /**
     * Look up param by key; used to access the correct sample value for DBTYPE.
     *
     * @param key
     * @return
     */
    public static Param getPram(String key) {
        for (Param param : getFactory().getParametersInfo()) {
            if (key.equals(param.key)) {
                return param;
            }
        }
        return null;
    }

    /**
     * A couple quick checks on the URL
     */
    public static final boolean isPostGIS(URL url) {
        if (url == null) {
            return false;
        }
        return url.getProtocol().toLowerCase().equals("postgis") //$NON-NLS-1$
                || url.getProtocol().toLowerCase().equals("postgis.jdbc") || //$NON-NLS-1$
                url.getProtocol().toLowerCase().equals("jdbc.postgis"); //$NON-NLS-1$
    }

    @Override
    public String reasonForFailure(URL url) {
        if (!isPostGIS(url))
            return Messages.PostGISServiceExtension_badURL;
        return reasonForFailure(createParams(url));
    }

    @Override
    protected String doOtherChecks(Map<String, Serializable> params) {
        if (!"postgis".equals(params.get(DBTYPE.key))) { //$NON-NLS-1$
            return format("Parameter DBTYPE is required to be \"{0}\"", DBTYPE.sample); //$NON-NLS-1$
        }

        // if the testing parameter is in params then this is
        // a recursive call originating in processParams and should be shorted
        // to prevent infinite loop.
        if (params.containsKey(IN_TESTING)) {
            return null;
        }

        Pair<Map<String, Serializable>, String> resultOfSplit = processParams(params);
        if (resultOfSplit.getRight() != null) {
            String reason = resultOfSplit.getRight();
            return reason;
        }
        return null;
    }

    private Pair<Map<String, Serializable>, String> processParams(
            Map<String, Serializable> params) {
        String schemasString = (String) params.get(SCHEMA.key);

        Set<String> goodSchemas = new HashSet<>();

        HashMap<String, Serializable> testedParams = new HashMap<>(params);
        testedParams.put(SCHEMA.key, "public"); //$NON-NLS-1$
        testedParams.put(IN_TESTING, true);
        String reason = super.reasonForFailure(testedParams);

        if (reason == null) {
            goodSchemas.add("public"); //$NON-NLS-1$
        }

        String[] schemas = schemasString.split(","); //$NON-NLS-1$

        for (String string : schemas) {
            if (!goodSchemas.contains(string)) {
                testedParams = new HashMap<>(testedParams);
                String trimmedSchema = string.trim();
                testedParams.put(SCHEMA.key, trimmedSchema);

                String reasonForFailure = super.reasonForFailure(testedParams);
                if (reasonForFailure == null) {
                    goodSchemas.add(string);
                } else {
                    reason = reasonForFailure;
                }
            }
        }

        if (!goodSchemas.isEmpty()) {
            testedParams.put(SCHEMA.key, combineSchemaStrings(goodSchemas));
        }

        testedParams.remove(IN_TESTING);

        Pair<Map<String, Serializable>, String> result;
        result = new Pair<>(testedParams, reason);
        return result;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactory() {
        return getFactory();
    };

    private Serializable combineSchemaStrings(Set<String> goodSchemas) {
        StringBuilder builder = new StringBuilder();
        for (String string : goodSchemas) {
            if (builder.length() > 0) {
                builder.append(',');
            }

            builder.append(string);
        }

        return builder.toString();
    }

}
