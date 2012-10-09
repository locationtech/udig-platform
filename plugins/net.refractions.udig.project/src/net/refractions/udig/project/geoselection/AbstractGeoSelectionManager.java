/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.geoselection;

import org.eclipse.core.runtime.ListenerList;

/**
 * General abstract implementation of <code>IGeoSelectionManager</code> interface.
 * 
 * @author vitalus
 * @version 0.1
 * @since UDIG 1.1
 */
public abstract class AbstractGeoSelectionManager implements IGeoSelectionManager {

    protected ListenerList listeners = new ListenerList();

    /**
     * Empty constructor
     */
    protected AbstractGeoSelectionManager() {

    }

    public void addListener( IGeoSelectionChangedListener listener ) {
        listeners.add(listener);
    }

    public void removeListener( IGeoSelectionChangedListener listener ) {
        listeners.remove(listener);
    }

}
