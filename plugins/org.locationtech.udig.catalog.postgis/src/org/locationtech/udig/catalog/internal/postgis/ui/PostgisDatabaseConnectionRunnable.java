/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.postgis.ui;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;
import static org.geotools.jdbc.JDBCDataStoreFactory.DBTYPE;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.locationtech.udig.catalog.PostgisServiceExtension2;
import org.locationtech.udig.catalog.internal.postgis.PostgisPlugin;
import org.locationtech.udig.catalog.service.database.DatabaseConnectionRunnable;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A runnable that attempts to connect to a postgis database. If it does it will get a list of all
 * the databases and store them for later access. If it does not then it will store an error
 * message.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class PostgisDatabaseConnectionRunnable implements DatabaseConnectionRunnable {

    private volatile boolean ran = false;
    private volatile String result = null;
    private final Set<String> databaseNames = new HashSet<String>();
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public PostgisDatabaseConnectionRunnable( String host2, int port2, String username2,
            String password2 ) {
        this.host = host2;
        this.port = port2;
        this.username = username2;
        this.password = password2;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {

        try {
            
            Map<String,Serializable> params = new HashMap<String,Serializable>();
            params.put( DBTYPE.key, (Serializable) new PostgisServiceDialect().dbType );
            params.put( HOST.key, host );            
            params.put( PORT.key, port );
            params.put( USER.key, username );
            params.put( PASSWD.key, password );
            params.put( DATABASE.key, "template1");
            
            BasicDataSource source = PostgisServiceExtension2.getFactory().createDataSource(params );
            Connection connection = source.getConnection();
            try {
                
                Statement statement = connection.createStatement();
                if (statement.execute("SELECT datname FROM pg_database")) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        databaseNames.add(resultSet.getString("datname"));
                    }
                }
                statement.close();
            } finally {
                if( connection != null ){
                    connection.close();
                }
                if( source != null ){
                    source.close();
                }
            }
        }
        catch (SQLException e) {
            checkSqlException(e);
        } catch (IOException e) {
            if( e.getCause() instanceof SQLException){
                checkSqlException((SQLException) e.getCause());
            }else{
                PostgisPlugin.log("Error connecting to datasource", e);
                result = "Unrecognized connection failure.  Check parameters and database.";
            }
        }
        ran = true;
    }

    private void checkSqlException( SQLException e ) {
        if( e.getMessage().contains("FATAL: no pg_hba.conf entry for host") && e.getMessage().contains("template1") ){ //$NON-NLS-1$ //$NON-NLS-2$
            // this is understandable the template1 database is not accessible to this user/location so it is not an error
        }else if( e.getMessage().contains("FATAL: role") && e.getMessage().contains("does not exist") ){  //$NON-NLS-1$//$NON-NLS-2$
                // this is understandable the template1 database is not accessible to this user/location so it is not an error
            result = "Username or password is incorrect";
        }else {
            PostgisPlugin.log("Error connecting to database template1", e);
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
        return databaseNames.toArray(new String[0]);
    }

}
