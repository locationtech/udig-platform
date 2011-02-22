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
package net.refractions.udig.catalog.internal.mysql.ui;

import java.io.Serializable;
import java.util.Map;

/**
 * One of the tabs in the PostGis Connection wizard page
 *
 * @author jesse
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public interface Tab {

    /**
     * Called by the {@link MySQLConnectionPage} as the page is about to be left if the tab is active
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
     * @return true if it is acceptable to leave the page false if the page must not be left
     */
    public boolean leavingPage();


    /**
     * Returns the connection parameters stored by the connection page.
     *
     * @return A map of connection parameters.
     */
    Map<String, Serializable> getParams();

}
