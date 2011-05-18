package eu.udig.catalog.teradata;

import static net.refractions.udig.catalog.service.database.ExtraParams.combo;
import static net.refractions.udig.catalog.service.database.ExtraParams.text;
import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.util.Arrays;
import java.util.List;

import net.refractions.udig.catalog.service.database.DatabaseConnectionRunnable;
import net.refractions.udig.catalog.service.database.DatabaseServiceDialect;
import net.refractions.udig.catalog.service.database.DatabaseWizardLocalization;
import net.refractions.udig.catalog.service.database.ExtraParams;
import net.refractions.udig.catalog.service.database.LookUpSchemaRunnable;

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
