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

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.catalog.service.database.DatabaseConnectionRunnable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A runnable that attempts to connect to a mysql database. If it does it will get a list of all
 * the databases and store them for later access. If it does not then it will store an error
 * message.
 * 
 * @author jesse
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySqlDatabaseConnectionRunnable implements DatabaseConnectionRunnable {

    private volatile boolean ran = false;
    private volatile String result = null;
    private final Set<String> databaseNames = new HashSet<String>();
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public MySqlDatabaseConnectionRunnable( String host2, int port2, String username2,
            String password2 ) {
        this.host = host2;
        this.port = port2;
        this.username = username2;
        this.password = password2;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {
    	
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(host);
        ds.setPort(port);
        ds.setDatabaseName("mysql"); //$NON-NLS-1$
        ds.setUser(username);
        ds.setPassword(password);
        
        DriverManager.setLoginTimeout(3);

        try {
            Connection connection = ds.getConnection();
            try {
                
                // TODO check that geometryColumns is accessible
            	ResultSet rs = connection.getMetaData().getCatalogs();
            	while(rs.next()){
            		databaseNames.add(rs.getString(1));              
                }
               
            } finally {
                connection.close();
            }

        } catch (SQLException e) {
            result = "Unrecognized connection failure: " + e.getMessage() + "\nCheck parameters and database."; //$NON-NLS-1$ //$NON-NLS-2$
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
                    "run must complete running before this method is called."); //$NON-NLS-1$
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
