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
package org.locationtech.udig.catalog.internal.oracle.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.internal.oracle.OracleServiceExtension;
import org.locationtech.udig.catalog.internal.oracle.OracleServiceImpl;
import org.locationtech.udig.catalog.ui.AbstractUDIGConnectionFactory;

/**
 * This appears to be glue code added by Jesse.
 *
 * @since 1.2.0
 */
public class OracleSpatialConnectionFactory extends AbstractUDIGConnectionFactory {

    @Override
    protected Map<String, Serializable> doCreateConnectionParameters(Object context) {
        if (context instanceof OracleServiceImpl) {
            OracleServiceImpl oracle = (OracleServiceImpl) context;
            return oracle.getConnectionParams();
        }
        // We need to check the provided object (probably a URL) and ensure it is meant for us
        ID id = ID.cast(context);
        if (id.toString().indexOf("oracle") != -1) { //$NON-NLS-1$

        }
        return null;
    }

    @Override
    protected URL doCreateConnectionURL(Object context) {
        return null;
    }

    @Override
    protected boolean doOtherChecks(Object context) {
        return false;
    }

    @Override
    protected ServiceExtension2 getServiceExtension() {
        return new OracleServiceExtension();
    }

}
