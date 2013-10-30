/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import static org.locationtech.udig.catalog.service.database.ExtraParams.combo;
import static org.locationtech.udig.catalog.service.database.ExtraParams.text;
import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.util.Arrays;
import java.util.List;

import org.locationtech.udig.catalog.service.database.DatabaseConnectionRunnable;
import org.locationtech.udig.catalog.service.database.DatabaseServiceDialect;
import org.locationtech.udig.catalog.service.database.DatabaseWizardLocalization;
import org.locationtech.udig.catalog.service.database.ExtraParams;
import org.locationtech.udig.catalog.service.database.LookUpSchemaRunnable;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.geotools.data.teradata.TeradataDataStoreFactory;

public class TeradataDialect extends DatabaseServiceDialect {

	public TeradataDialect() {
		super(SCHEMA, DATABASE, HOST, PORT, USER, PASSWD, DBTYPE, DBTYPE.sample
				.toString(), "jdbc:teradata", new DatabaseWizardLocalization());
	}

	@Override
	public IDialogSettings getDialogSetting() {
		return Activator.getDefault().getDialogSettings();
	}

	@Override
	public void log(String message, Throwable e) {
		Activator.log(message, e);
	}

	@Override
	public DatabaseConnectionRunnable createDatabaseConnectionRunnable(
			String host, int port, String username, String password) {
		return new TeradataDatabaseConnectionRunnable(host, port, username,
				password, localization);
	}

	@Override
	public LookUpSchemaRunnable createLookupSchemaRunnable(String host,
			int port, String username, String password, String database) {
		return new TeradataLookUpSchemaRunnable(host, port, username, password,
				database);
	}

	@Override
	protected List<ExtraParams> hostPageExtraParams() {
		return Arrays.asList(
				combo("Connection Mode",TeradataDataStoreFactory.TMODE, null, "ANSI","Teradata"),
				text("Query Band application",TeradataDataStoreFactory.APPLICATION, "uDig")
				
		);
	}

}
