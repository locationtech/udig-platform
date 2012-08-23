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
package net.refractions.udig.project.ui.internal.handlers;

import net.refractions.udig.project.IMap;
import net.refractions.udig.ui.properties.ProperitesCommandHandler;

/**
 * Command hander for the MapProperties command, opens a Property Dialog focused on the IMap associated with
 * the current selection.
 * 
 * @author jesse
 * @since 1.1.0
 * @version 1.3.2
 */
public class MapPropertiesCommandHandler extends ProperitesCommandHandler {
    public MapPropertiesCommandHandler(){
        super( IMap.class );
    }

}
