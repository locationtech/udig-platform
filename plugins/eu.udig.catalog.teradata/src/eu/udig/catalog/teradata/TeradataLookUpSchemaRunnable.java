/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package eu.udig.catalog.teradata;

import static java.text.MessageFormat.format;
import static org.geotools.data.teradata.TeradataDataStoreFactory.DBTYPE;
import static org.geotools.data.teradata.TeradataDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.catalog.service.database.LookUpSchemaRunnable;
import net.refractions.udig.catalog.service.database.TableDescriptor;
import net.refractions.udig.core.Pair;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataSourceException;
import org.geotools.data.teradata.TeradataDataStoreFactory;

/**
 * A runnable that looks up all the schemas in the provided database using the
 * provided username and password
 * 
 * @author jesse
 * @since 1.1.0
 */
public class TeradataLookUpSchemaRunnable implements LookUpSchemaRunnable {

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String database;
	private final Set<TableDescriptor> tables = new HashSet<TableDescriptor>();
	private volatile String error;
	private volatile boolean ran = false;

	public TeradataLookUpSchemaRunnable(String host, int port, String username,
			String password, String database) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
	}

	public void run(IProgressMonitor monitor) {

		monitor.beginTask("Loading Database tables", 3);
		monitor.worked(1);

		try {
			loadTableDescriptors();

		} catch (SQLException e) {
			error = "An error occurred while looking up list of tables.  Check that you entered the correct username and database name.";
			Activator.log("error", e);
		} catch (DataSourceException e) {
			error = "An error occurred while looking up list of tables.  Check that you entered the correct username and database name.";
			Activator.log("error", e);
		}
		monitor.done();
		ran = true;
	}

	private void loadTableDescriptors() throws SQLException,
			DataSourceException {

		TeradataDataStoreFactory factory = TeradataServiceExtension
				.getFactory();
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put(DBTYPE.key, (Serializable) DBTYPE.sample);
		params.put(HOST.key, host);
		params.put(PORT.key, port);
		params.put(USER.key, username);
		params.put(PASSWD.key, password);
		params.put(DATABASE.key, database);

		BasicDataSource dataSource = null;
		Connection connection = null;
		try {
			dataSource = factory.createDataSource(params);
			connection = dataSource.getConnection();

			Statement statement = connection.createStatement();

			if (!hasWritableTable(
					"SYSSPATIAL.spatial_ref_sys", "SRID", statement)) { //$NON-NLS-1$
				error = "The 'srid' table is either missing or not accessible; the Teradata datastore cannot work without the srid table.  Please talk to your database administrator.";
				return;
			}

			// Pair is schema, table name
			List<Pair<String, String>> tableNames = new ArrayList<Pair<String, String>>();

			ResultSet resultSet = statement
					.executeQuery("SELECT F_TABLE_NAME,f_geometry_column FROM SYSSPATIAL.GEOMETRY_COLUMNS ORDER BY F_TABLE_NAME;");
			while (resultSet.next()) {
				String schema = database; //$NON-NLS-1$
				String table = resultSet.getString(1); //$NON-NLS-1$
				if (hasWritableTable(database+"."+table, resultSet.getString(2), statement)) { //$NON-NLS-1$
					tableNames.add(Pair.create(schema, table));
				}
			}
			if(tableNames.size() > 0) {
				Collection<TableDescriptor> results = lookupGeometryColumn(
						tableNames, connection);
				tables.addAll(results);
			}
			statement.close();
		} catch (SQLException e) {
			error = "An error occurred when querying the database about the data it contains. Please talk to the administrator: "
					+ e.getMessage();
		} catch (IOException io) {
			error = "An error occurred when querying the database about the data it contains. Please talk to the administrator: "
					+ io.getMessage();
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (dataSource != null) {
				dataSource.close();
			}
		}
	}

	private boolean hasWritableTable(String tablename, String column,
			Statement statement) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("select top 1 ");
			stringBuilder.append(column);
			stringBuilder.append(" from ");
			stringBuilder.append(tablename);
			statement.executeQuery(stringBuilder.toString());
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	private Set<TableDescriptor> lookupGeometryColumn(
			List<Pair<String, String>> tablenames, Connection connection)
			throws SQLException {
		final String f_geometry_column = "f_geometry_column";
		final String geomTypeCol = "geom_type";
		final String sridCol = "srid";
		final String f_table_name = "f_table_name";
		final String f_table_schema = "f_table_schema";
		Statement statement = connection.createStatement();
		
		TeradataDialect dialect = new TeradataDialect();

		try {
			StringBuilder where = new StringBuilder();
			final String wherePattern = " ( {0}=''{1}'' AND {2}=''{3}'')";
			for (Pair<String, String> pair : tablenames) {
				if (where.length() > 0) {
					where.append(" or ");
				}
				String schema = pair.left();
				String table = pair.right();
				where.append(format(wherePattern, f_table_name, table,
						f_table_schema, schema));
			}
			final String sql = "SELECT {0}, {1}, {2}, {3}, {4} FROM SYSSPATIAL.GEOMETRY_COLUMNS WHERE {5};";
			ResultSet results = statement.executeQuery(format(sql,
					f_geometry_column, geomTypeCol, sridCol, f_table_name,
					f_table_schema, where));
			while (results.next()) {
				String srid = results.getString(sridCol);
				String geomType = results.getString(geomTypeCol);
				String geom = results.getString(f_geometry_column);
				String table = results.getString(f_table_name);
				String schema = results.getString(f_table_schema);

				boolean broken = isBroken(connection, table, schema, geom,
						geomType);
				tables.add(new TableDescriptor(table, dialect.toGeomClass(geomType), schema, geom,
						srid, broken));

			}

			return tables;
		} finally {
			statement.close();
		}
	}

	private boolean isBroken(Connection connection, String table,
			String schema, String geom, String type) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			String sql = "select " + geom + " from " + schema + "." + table
					+ " limit 0";
			ResultSet results = statement.executeQuery(sql);
			String columnType = results.getMetaData().getColumnTypeName(1);
			return !(columnType.equalsIgnoreCase(type)
					|| columnType.equalsIgnoreCase("geometry")
					|| columnType.equalsIgnoreCase("geometry[]")
					|| columnType.equalsIgnoreCase("point")
					|| columnType.equalsIgnoreCase("point[]")
					|| columnType.equalsIgnoreCase("line")
					|| columnType.equalsIgnoreCase("line[]")
					|| columnType.equalsIgnoreCase("polygon") || columnType
					.equalsIgnoreCase("polygon[]"));

		} catch (SQLException e) {
			return false;
		} finally {
			statement.close();
		}

	}

	/**
	 * Returns null if the run method was able to connect to the database
	 * otherwise will return a message indicating what went wrong.
	 * 
	 * @return null if the run method was able to connect to the database
	 *         otherwise will return a message indicating what went wrong.
	 * @throws IllegalStateException
	 *             if called before run.
	 */
	public String getError() throws IllegalStateException {
		if (!ran) {
			throw new IllegalStateException(
					"run must complete running before this method is called.");
		}
		return error;
	}

	/**
	 * Returns the names of the databases in the database that this object
	 * connected to when the run method was executed.
	 * 
	 * @return the names of the databases in the database that this object
	 *         connected to when the run method was executed.
	 */
	public Set<TableDescriptor> getTableDescriptors() {
		return tables;
	}

}
