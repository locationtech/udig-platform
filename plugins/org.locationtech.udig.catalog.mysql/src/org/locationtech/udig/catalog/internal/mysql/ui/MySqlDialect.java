/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.mysql.ui;

import static org.geotools.data.mysql.MySQLDataStoreFactory.*;
import org.eclipse.jface.dialogs.IDialogSettings;

import org.locationtech.udig.catalog.MySQLServiceExtension;
import org.locationtech.udig.catalog.internal.mysql.MySQLPlugin;
import org.locationtech.udig.catalog.service.database.DatabaseConnectionRunnable;
import org.locationtech.udig.catalog.service.database.DatabaseServiceDialect;
import org.locationtech.udig.catalog.service.database.DatabaseWizardLocalization;
import org.locationtech.udig.catalog.service.database.LookUpSchemaRunnable;

/**
 * All the mysql specific code for working for the new wizard
 * 
 * @author jesse
 * @since 1.1.0
 */
public class MySqlDialect extends DatabaseServiceDialect {

    public MySqlDialect(  ) {
        // TODO customize localization if it needs it
        super( null, DATABASE, HOST, PORT, USER, PASSWD, MySQLServiceExtension.getPram( DBTYPE.key ), null,
                "jdbc.mysql", new DatabaseWizardLocalization()); //$NON-NLS-1$
    }

    @Override
    public DatabaseConnectionRunnable createDatabaseConnectionRunnable( String host, int port,
            String username, String password ) {
        return new MySqlDatabaseConnectionRunnable(host, port, username, password);
    }

    @Override
    public LookUpSchemaRunnable createLookupSchemaRunnable( String host, int port, String username,
            String password, String database ) {
        return new MySqlLookUpSchemaRunnable(host,port,username,password,database);
    }

    @Override
    public IDialogSettings getDialogSetting() {
        return MySQLPlugin.getDefault().getDialogSettings();
    }

    @Override
    public void log( String message, Throwable e ) {
        MySQLPlugin.log(message, e);
    }

}
