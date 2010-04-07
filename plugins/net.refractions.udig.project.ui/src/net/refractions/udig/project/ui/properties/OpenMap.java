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
package net.refractions.udig.project.ui.properties;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.ui.operations.IOpFilterListener;
import net.refractions.udig.ui.operations.PropertyValue;

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
