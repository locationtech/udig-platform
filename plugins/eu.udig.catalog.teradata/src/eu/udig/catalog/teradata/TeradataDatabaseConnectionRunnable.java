package eu.udig.catalog.teradata;

import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.refractions.udig.catalog.service.database.DatabaseConnectionRunnable;
import net.refractions.udig.catalog.service.database.DatabaseWizardLocalization;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.IProgressMonitor;

public class TeradataDatabaseConnectionRunnable implements
		DatabaseConnectionRunnable {

	private String host;
	private int port;
	private String username;
	private String password;
	private DatabaseWizardLocalization localization;
	private boolean ran = false;
	private String result;
	private Set<String> databaseNames = new TreeSet<String>();

	public TeradataDatabaseConnectionRunnable(String host, int port,
			String username, String password,
			DatabaseWizardLocalization localization) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.localization = localization;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {

			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put(DBTYPE.key, (Serializable) new TeradataDialect().dbType);
			params.put(HOST.key, host);
			params.put(PORT.key, port);
			params.put(USER.key, username);
			params.put(PASSWD.key, password);
			params.put(DATABASE.key, "dbc");

			BasicDataSource source = TeradataServiceExtension.getFactory()
					.createDataSource(params);
			Connection connection = source.getConnection();
			try {

				Statement statement = connection.createStatement();
				if (statement
						.execute("SELECT F_TABLE_SCHEMA FROM SYSSPATIAL.GEOMETRY_COLUMNS")) {
					ResultSet resultSet = statement.getResultSet();
					while (resultSet.next()) {
						databaseNames.add(resultSet.getString(1).trim());
					}
				}
				statement.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
				if (source != null) {
					source.close();
				}
			}
		} catch (SQLException e) {
			checkSqlException(e);
		} catch (Exception e) {
			if (e.getCause() instanceof SQLException) {
				checkSqlException((SQLException) e.getCause());
			} else {
				Activator.log("Error connecting to datasource", e);
				result = "Unrecognized connection failure.  Check parameters and database.";
			}
		}
		ran = true;
	}

	private void checkSqlException(SQLException e) {
		if (e.getMessage().contains("[Error 3807]")) {
			result = "SYSSPATIAL.GEOMETRY_COLUMNS is either inaccessible or does not exist.  The table must both exist and be accessible to current.";
		} else if (e.getMessage().contains("[Error 8017]")) { //$NON-NLS-1$//$NON-NLS-2$
			// this is understandable the template1 database is not accessible
			// to this user/location so it is not an error
			result = "Username or password is incorrect";
		} else {
			Activator.log("Error connecting to database dbc", e);
			result = "Unrecognized connection failure.  Check parameters and database.";
		}
	}

	public String canConnect() throws IllegalStateException {
		if (!ran) {
			throw new IllegalStateException(
					"run must complete running before this method is called.");
		}
		return result;
	}

	public String[] getDatabaseNames() {
		if (!ran) {
			throw new IllegalStateException(
					"run must complete running before this method is called.");
		}
		return databaseNames.toArray(new String[0]);
	}

}
