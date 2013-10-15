/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.service.database;

import org.eclipse.jface.operation.IRunnableWithProgress;
/**
 * A runnable that attempts to connect to a database. If it does it will get a list of all
 * the databases and store them for later access. If it does not then it will store an error
 * message.
 * 
 * @author jesse
 * @since 1.1.0
 */

public interface DatabaseConnectionRunnable extends IRunnableWithProgress {
    /**
     * Returns null if the run method was able to connect to the database otherwise will return a
     * message indicating what went wrong.
     * 
     * @return null if the run method was able to connect to the database otherwise will return a
     *         message indicating what went wrong.
     * @throws IllegalStateException if called before run.
     */
    String canConnect();

    /**
     * Returns the names of the databases in the database that this object connected to when the run
     * method was executed.
     * 
     * @return the names of the databases in the database that this object connected to when the run
     *         method was executed.
     */
    String[] getDatabaseNames();

}
