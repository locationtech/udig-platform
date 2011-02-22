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
package net.refractions.udig.catalog.internal.postgis.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.data.postgis.PostgisConnectionFactory;

/**
 * A runnable that looks up all the schemas in the provided database using the provided username and
 * password
 *
 * @author jesse
 * @since 1.1.0
 */
public class LookUpSchemaRunnable implements IRunnableWithProgress {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private final Set<PostgisTableDescriptor> tables = new HashSet<PostgisTableDescriptor>();
    private volatile String error;
    private volatile boolean ran = false;

    public LookUpSchemaRunnable( String host, int port, String username, String password,
            String database ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void run( IProgressMonitor monitor ) {
        PostgisConnectionFactory conFac = new PostgisConnectionFactory(host, port, database);
        conFac.setLogin(username, password);
        DriverManager.setLoginTimeout(3);

        try {
            loadTableDescriptrs(conFac);

        } catch (SQLException e) {
            error = "Unrecognized connection failure.  Check parameters and database.";
        }
        ran = true;
    }

    private void loadTableDescriptrs( PostgisConnectionFactory conFac ) throws SQLException {
        Connection connection = conFac.getConnection();
        try {

            Statement statement = connection.createStatement();
            if (statement
                    .execute("SELECT schemaname, tablename FROM pg_tables ORDER BY schemaname, tablename;")) {
                ResultSet resultSet = statement.getResultSet();
                while( resultSet.next() ) {
                    String schema = resultSet.getString("schemaname");
                    String table = resultSet.getString("tablename");
                    Pair<String, Pair<String, String>> results = lookupGeometryColumn(table,
                            schema, connection);
                    if (results != null) {
                        String geometryColumn = results.getLeft();
                        String geometryType = results.getRight().getLeft();
                        String srid = results.getRight().getRight();
                        tables.add(new PostgisTableDescriptor(table, geometryType, schema,
                                geometryColumn, srid));
                    }

                }
            }
            statement.close();
        } catch (SQLException e) {
            error = "An error occurred when querying the database about the data it contains. Please talk to the administrator: "
                    + e.getMessage();
        } finally {
            connection.close();
        }
    }

    private Pair<String, Pair<String, String>> lookupGeometryColumn( String table, String schema,
            Connection connection ) throws SQLException {

        Statement statement = connection.createStatement();
        try {
            String f_geometry_column = "f_geometry_column";
            String type = "type";
            String srid = "srid";
            String sql = "SELECT " + f_geometry_column + ", " + type + ", " + srid
                    + " FROM geometry_columns WHERE f_table_name='" + table
                    + "' AND f_table_schema='" + schema + "';";
            if (statement.execute(sql)) {
                ResultSet results = statement.getResultSet();
                if (results.next()) {
                    Pair<String, String> typeSrid = new Pair<String, String>(results
                            .getString(type), results.getString(srid));
                    String geom = results.getString(f_geometry_column);
                    Pair<String, Pair<String, String>> all = new Pair<String, Pair<String, String>>(
                            geom, typeSrid);
                    return all;
                }
            }

            return null;
        } finally {
            statement.close();
        }
    }
    /**
     * Returns null if the run method was able to connect to the database otherwise will return a
     * message indicating what went wrong.
     *
     * @return null if the run method was able to connect to the database otherwise will return a
     *         message indicating what went wrong.
     * @throws IllegalStateException if called before run.
     */
    public String getError() throws IllegalStateException {
        if (!ran) {
            throw new IllegalStateException(
                    "run must complete running before this method is called.");
        }
        return error;
    }

    /**
     * Returns the names of the databases in the database that this object connected to when the run
     * method was executed.
     *
     * @return the names of the databases in the database that this object connected to when the run
     *         method was executed.
     */
    public Set<PostgisTableDescriptor> getSchemas() {
        return tables;
    }

}
