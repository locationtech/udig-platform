/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt;


import net.refractions.udig.catalog.IServiceInfo;


import org.eclipse.core.runtime.IProgressMonitor;


class WMTServiceInfo extends IServiceInfo {

    WMTServiceInfo( WMTService service, IProgressMonitor monitor) {
        this.title = service.getIdentifier().toString();
        this.description = "Web Map Tiles"; //$NON-NLS-1$
        this.keywords = new String[]{"Web Map Tiles"};         //$NON-NLS-1$
    }
    
}