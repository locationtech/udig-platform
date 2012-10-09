/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.internal.db2.ui;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.internal.db2.DB2ServiceExtension;
import net.refractions.udig.catalog.ui.AbstractUDIGConnectionFactory;

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
