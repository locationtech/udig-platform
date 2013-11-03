/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.service.database;

import java.util.Set;

import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * This runnable looks up the objects (typically tables and schemas) that can be used to
 * create layers from.  The objects should be verified as valid as much as possible.  This runnable
 * is ran in an isolated thread so it can connect to the database.
 * 
 *  <p> Please make sure to test against sources that are slow to connect to (slow ping) to
 *  ensure good user experience</p>
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface LookUpSchemaRunnable extends IRunnableWithProgress {

    /**
     * Returns null if the run method was able to connect to the database otherwise will return a
     * message indicating what went wrong.
     * 
     * @return null if the run method was able to connect to the database otherwise will return a
     *         message indicating what went wrong.
     * @throws IllegalStateException if called before run.
     */
    String getError() throws IllegalStateException;
    
    /**
     * Returns the names of the databases in the database that this object connected to when the run
     * method was executed.
     * 
     * @return the names of the databases in the database that this object connected to when the run
     *         method was executed.
     */
    Set<TableDescriptor> getTableDescriptors();
}
