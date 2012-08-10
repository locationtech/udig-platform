/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011-2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.ui.internal;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.ui.properties.ProperitesCommandHandler;

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