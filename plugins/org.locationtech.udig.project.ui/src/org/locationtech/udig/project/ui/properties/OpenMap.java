/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.properties;

import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.operations.IOpFilterListener;
import org.locationtech.udig.ui.operations.PropertyValue;

/**
 * Returns true if there is an open map.
 * @author Jesse
 * @since 1.1.0
 */
public class OpenMap implements PropertyValue {

    public boolean isTrue( Object object, String value ) {
        return ApplicationGIS.getActiveMap()!=ApplicationGIS.NO_MAP;
    }

    public void addListener( IOpFilterListener listener ) {
    }

    public boolean canCacheResult() {
        return false;
    }

    public boolean isBlocking() {
        return false;
    }

    public void removeListener( IOpFilterListener listener ) {
    }

}
