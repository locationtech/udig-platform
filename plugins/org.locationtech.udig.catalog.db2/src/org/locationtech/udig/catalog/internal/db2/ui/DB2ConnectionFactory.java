/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.db2.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.internal.db2.DB2ServiceExtension;
import org.locationtech.udig.catalog.ui.AbstractUDIGConnectionFactory;

public class DB2ConnectionFactory extends AbstractUDIGConnectionFactory {

    @Override
    protected Map<String, Serializable> doCreateConnectionParameters( Object context ) {
        return null;
    }

    @Override
    protected URL doCreateConnectionURL( Object context ) {
        return null;
    }

    @Override
    protected boolean doOtherChecks( Object context ) {
        return false;
    }

    @Override
    protected ServiceExtension2 getServiceExtension() {
        return new DB2ServiceExtension();
    }
}
