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
package net.refractions.udig.catalog.service.database;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import net.refractions.udig.core.Either;

import org.eclipse.swt.widgets.Listener;

/**
 * One of the tabs in the PostGis Connection wizard page
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface Tab {

    /**
     * Called by the {@link PostgisConnectionPage} as the page is about to be left if the tab is active.
     * <p>
     * There are two main use cases for this method. The first is to save settings for the next time
     * the wizard is visited. The other is to perform some checks or do some loading that is too expensive to do every
     * time isPageComplete() is called.  For example a database wizard page might try to connect to the database in this method
     * rather than isPageComplete() because it is such an expensive method to call.
     * </p>
     * <p>
     * If an expensive method is called make sure to run it in the container:
     *         <pre>getContainer().run(true, cancelable, runnable);</pre>
     * </p>
     * 
     * @return true if it is acceptable to leave the page; false if the page must not be left
     */
    public boolean leavingPage();
    
    /**
     * Returns the connection parameters as known by the caller.  
     * @param params the basic parameters for connecting to a database.  The tab can augment the params. 
     * 
     * @return An error message or a map of connection parameters if all goes right.
     */
    Either<String,Map<String, Serializable>> getParams(Map<String, Serializable> params);
    
    /**
     * Return the ids of the selected IGeoResource (if needed)
     * 
     * @param params the params used for connecting to the database.  It must be a valid set of Postgis params.
     * 
     * @return the ids of the selected IGeoResources
     */
    public Collection<URL> getResourceIDs(Map<String, Serializable> params);
    
    /**
     * Fires an SWT.Modify method when something has changed that may change the state of the tab as recognizable
     * by calling {@link #getParams(Map)} and {@link #getResourceIDs(Map)}.  
     * When an event is fired the owner of the tab should call {@link #getParams(Map)} and {@link #getResourceIDs(Map)} again.
     *
     * @param modifyListener
     */
    public void addListener( Listener modifyListener );

    /**
     * Called when the containing page has just been shown.  The tab should clear cached information 
     */
    public void init();
}
