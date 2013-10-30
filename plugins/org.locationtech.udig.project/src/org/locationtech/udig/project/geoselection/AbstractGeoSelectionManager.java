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
package org.locationtech.udig.project.geoselection;

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
