/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.internal;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.ui.properties.ProperitesCommandHandler;

/**
 * A command hander to display an IGeoResource properties page.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class IGeoResourcePropertiesCommandHandler extends ProperitesCommandHandler {
    public IGeoResourcePropertiesCommandHandler() {
        super(IGeoResource.class);
    }
}
