/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.handlers;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.ui.properties.ProperitesCommandHandler;

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
