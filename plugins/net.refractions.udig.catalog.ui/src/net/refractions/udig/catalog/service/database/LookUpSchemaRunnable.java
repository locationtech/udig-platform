/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.service.database;

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
