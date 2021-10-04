/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.DataStoreFactorySpi;
import org.locationtech.udig.catalog.internal.Messages;

/**
 * A support class for creating Service Extensions based on GeoTools datastores. Provides feedback
 * information for when the datastore cannot use the offered parameters
 *
 * @author Jesse
 * @since 1.1.0
 */
public abstract class AbstractDataStoreServiceExtension implements ServiceExtension2 {

    protected class ParamInfo {

        public String the_schema;

        public Integer the_port;

        public String host;

        public String the_database;

        public String password;

        public String username;

        public String protocol;

        public ParamInfo(String protocol, String username, String password, String host,
                String the_database, Integer the_port, String the_schema) {
            this.protocol = protocol;
            this.username = username;
            this.password = password;
            this.the_database = the_database;
            this.host = host;
            this.the_port = the_port;
            this.the_schema = the_schema;

        }

    }

    @Override
    public final String reasonForFailure(Map<String, Serializable> params) {
        String parameterProcessingResult = processParameters(params,
                getDataStoreFactory().getParametersInfo());
        if (parameterProcessingResult != null)
            return parameterProcessingResult;

        String result = doOtherChecks(params);
        if (result != null)
            return result;
        return null;
    }

    /**
     * Do any other checks besides a basic parsing of the parameters.
     *
     * @param params parameters to be used for creating the datastore
     *
     * @return null if everything checks out
     */
    protected String doOtherChecks(Map<String, Serializable> params) {
        return null;
    }

    /**
     * Returns an instance of the datastore factory that can create the datastore.
     *
     * @return an instance of the datastore factory that can create the datastore.
     */
    protected abstract DataStoreFactorySpi getDataStoreFactory();

    @Override
    public abstract String reasonForFailure(URL url);

    private String processParameters(Map<String, Serializable> params, Param[] arrayParameters) {
        if (params == null) {
            return Messages.DataStoreServiceExtension_nullparams;
        }
        for (int i = 0; i < arrayParameters.length; i++) {
            Param param = arrayParameters[i];
            Object value;
            if (!params.containsKey(param.key)) {
                if (param.required) {
                    return param.key + Messages.DataStoreServiceExtension_missingKey
                            + param.description; // missing required key!
                } else {
                    continue;
                }
            }
            try {
                value = param.lookUp(params);
            } catch (IOException e) {
                // could not upconvert/parse to expected type!
                // even if this parameter is not required
                // we are going to refuse to process
                // these params
                return Messages.DataStoreServiceExtension_theParam + param.key
                        + Messages.DataStoreServiceExtension_wrongType + param.type
                        + Messages.DataStoreServiceExtension_butWas
                        + params.get(param.key).getClass();
            }
            if (value == null) {
                if (param.required) {
                    return param.key + Messages.DataStoreServiceExtension_nullParam
                            + param.description;
                }
            } else {
                if (!param.type.isInstance(value)) {
                    return Messages.DataStoreServiceExtension_theParam + param.key
                            + Messages.DataStoreServiceExtension_wrongType + param.type
                            + Messages.DataStoreServiceExtension_butWas
                            + params.get(param.key).getClass(); // value was not of the required
                                                                // type
                }
            }
        }
        return null;
    }

    /**
     * For special URLs like DB URLs. parses out the required information from the url. Consider:
     * jdbc.postgis://username:password@host:port/database/schema. it will parse out these parts
     * from the URL. In cases like oracle the equivalents have to be understood.
     *
     * @param url
     * @return
     */
    protected ParamInfo parseParamInfo(URL url) {
        String host = url.getHost();
        if (host != null && !"".equals(host)) { //$NON-NLS-1$
            if (host.endsWith("postgis.jdbc") || host.endsWith("jdbc.postgis")) { //$NON-NLS-1$ //$NON-NLS-2$
                host = host.substring(0, host.length() - 12);
            }
        }
        Integer the_port = url.getPort() == -1 ? Integer.valueOf(5432)
                : Integer.valueOf(url.getPort());
        String path = url.getPath();
        String the_database;
        String the_schema;
        if (path != null) {
            int endDB = path.indexOf('/', 1);
            the_database = path.substring(1, endDB);
            the_schema = path.substring(endDB + 1);
        } else {
            the_database = ""; //$NON-NLS-1$
            the_schema = null;
        }
        if (the_schema == null)
            the_schema = "public"; //$NON-NLS-1$

        String userInfo = url.getUserInfo() == null ? "" : url.getUserInfo(); //$NON-NLS-1$
        String username;
        String password;
        if (userInfo.contains(":")) { //$NON-NLS-1$
            int indexOf = userInfo.indexOf(':', 1);
            username = userInfo.substring(0, indexOf);
            password = userInfo.substring(indexOf + 1, userInfo.length());
        } else {
            username = userInfo;
            password = ""; //$NON-NLS-1$
        }
        return new ParamInfo(url.getProtocol(), username, password, host, the_database, the_port,
                the_schema);
    }

}
