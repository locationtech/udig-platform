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

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.data.postgis.PostgisConnectionFactory;

/**
 * A runnable that attempts to connect to a postgis database. If it does it will get a list of all
 * the databases and store them for later access. If it does not then it will store an error
 * message.
 *
 * @author jesse
 * @since 1.1.0
 */
public class DatabaseConnectionRunnable implements IRunnableWithProgress {

    private volatile boolean ran = false;
    private volatile String result = null;
    private final Set<String> databaseNames = new HashSet<String>();
    private final String host;
    private final String port;
    private final String username;
    private final String password;

    public DatabaseConnectionRunnable( String host2, String port2, String username2,
            String password2 ) {
        this.host = host2;
        this.port = port2;
        this.username = username2;
        this.password = password2;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {
        PostgisConnectionFactory conFac = new PostgisConnectionFactory(host, port, "template1");
        conFac.setLogin(username, password);
        DriverManager.setLoginTimeout(3);

        try {
            Connection connection = conFac.getConnection();
            try {

                // TODO check that geometryColumns is accessible
                Statement statement = connection.createStatement();
                if (statement.execute("SELECT datname FROM pg_database")) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        databaseNames.add(resultSet.getString("datname"));
                    }
                }
                statement.close();
            } finally {
                connection.close();
            }

        } catch (SQLException e) {
            // TODO handle case where template1 is not accessible
            if( e.getMessage().contains("FATAL: no pg_hba.conf entry for host") && e.getMessage().contains("template1") ){
                // this is understandable the template1 database is not accessible to this user/location so it is not an error
            }else {
                result = "Unrecognized connection failure.  Check parameters and database.";
            }
        }
        ran = true;
    }

    /**
     * Returns null if the run method was able to connect to the database otherwise will return a
     * message indicating what went wrong.
     *
     * @return null if the run method was able to connect to the database otherwise will return a
     *         message indicating what went wrong.
     * @throws IllegalStateException if called before run.
     */
    public String canConnect() throws IllegalStateException {
        if (!ran) {
            throw new IllegalStateException(
                    "run must complete running before this method is called.");
        }
        return result;
    }

    /**
     * Returns the names of the databases in the database that this object connected to when the run
     * method was executed.
     *
     * @return the names of the databases in the database that this object connected to when the run
     *         method was executed.
     */
    public String[] getDatabaseNames() {
        return databaseNames.toArray(new String[0]);
    }

}
