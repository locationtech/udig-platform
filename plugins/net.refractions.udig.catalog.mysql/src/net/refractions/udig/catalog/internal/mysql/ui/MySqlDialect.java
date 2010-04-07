/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.mysql.ui;

import static org.geotools.data.mysql.MySQLDataStoreFactory.*;
import org.eclipse.jface.dialogs.IDialogSettings;

import net.refractions.udig.catalog.MySQLServiceExtension;
import net.refractions.udig.catalog.internal.mysql.MySQLPlugin;
import net.refractions.udig.catalog.service.database.DatabaseConnectionRunnable;
import net.refractions.udig.catalog.service.database.DatabaseServiceDialect;
import net.refractions.udig.catalog.service.database.DatabaseWizardLocalization;
import net.refractions.udig.catalog.service.database.LookUpSchemaRunnable;

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
