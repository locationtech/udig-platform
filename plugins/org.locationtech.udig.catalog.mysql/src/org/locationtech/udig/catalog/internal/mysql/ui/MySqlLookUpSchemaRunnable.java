/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.mysql.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.catalog.service.database.LookUpSchemaRunnable;
import org.locationtech.udig.catalog.service.database.TableDescriptor;
import org.locationtech.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A runnable that looks up all the schemas in the provided database using the provided username and
 * password
 * 
 * @author jesse
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySqlLookUpSchemaRunnable implements LookUpSchemaRunnable {

    private final String[] geomTypes = {"geometry", "point", "multipoint", "line", "linestring", "polygon", "multipolygon"};
    
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private final Set<TableDescriptor> tables = new HashSet<TableDescriptor>();
    private volatile String error;
    private volatile boolean ran = false;

    public MySqlLookUpSchemaRunnable( String host, int port, String username, String password,
            String database ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void run( IProgressMonitor monitor ) {
        //MySQLConnectionFactory conFac = new MySQLConnectionFactory(host, port, database);
        //conFac.setLogin(username, password);
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(host);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(username);
        ds.setPassword(password);
        DriverManager.setLoginTimeout(3);

        try {
            loadTableDescriptrs(ds);

        } catch (SQLException e) {
            error = "Unrecognized connection failure.  Check parameters and database.";
        }
        ran = true;
    }

    private void loadTableDescriptrs( MysqlDataSource ds ) throws SQLException {
    	MySqlDialect dialect = new MySqlDialect();
        Connection connection = ds.getConnection(username, password);
        try {

            Statement statement = connection.createStatement();
            if (statement
                    .execute("SHOW TABLES;")) { //$NON-NLS-1$
                ResultSet resultSet = statement.getResultSet();
                while( resultSet.next() ) {
                    
                    String table = resultSet.getString("Tables_in_"+database); //$NON-NLS-1$
                    Pair<String, Pair<String, String>> results = lookupGeometryColumn(table,
                             connection);
                    if (results != null) {
                        String geometryColumn = results.getLeft();
                        String geometryType = results.getRight().getLeft();
                        String srid = results.getRight().getRight();
                        tables.add(new TableDescriptor(table, dialect.toGeomClass(geometryType), null,
                                geometryColumn, srid,false));
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

    private Pair<String, Pair<String, String>> lookupGeometryColumn( String table,
            Connection connection ) throws SQLException {

        Statement statement = connection.createStatement();
        try {

            String sql = "SHOW FIELDS FROM " + table + " ;";
            if (statement.execute(sql)) {
                ResultSet results = statement.getResultSet();
                while (results.next()) {
                    if ( existsInArray(results.getString("Type"), geomTypes, false)) {
                        Pair<String, String> typeSrid = new Pair<String, String>(results
                                .getString("Type"), "4362"); // assumed srid
                        String geom = results.getString("Field");
                        Pair<String, Pair<String, String>> all = new Pair<String, Pair<String, String>>(
                                geom, typeSrid);
                        return all;
                    }
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
    public Set<TableDescriptor> getTableDescriptors() {
        return tables;
    }

    /**
     * checks whether a String value exists in the provide array of Strings 
     * 
     * @param valuetoTest
     * @param array
     * @param caseSensitive flags to enable/disable case sensitivity during comparison
     * @return
     */
    private boolean existsInArray(String valuetoTest, String[] array, boolean caseSensitive) {

        for (String entry : array) {
            if (caseSensitive) {
                if (entry.equals(valuetoTest)) {
                    return true;
                }
            } else {
                if (entry.equalsIgnoreCase(valuetoTest)) {
                    return true;
                }
            }
        }
        return false;
    }
}
