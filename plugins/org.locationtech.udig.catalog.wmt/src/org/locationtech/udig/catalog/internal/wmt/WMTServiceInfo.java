/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008-2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt;


import org.locationtech.udig.catalog.IServiceInfo;


import org.eclipse.core.runtime.IProgressMonitor;


class WMTServiceInfo extends IServiceInfo {

    WMTServiceInfo( WMTService service, IProgressMonitor monitor) {
        this.title = service.getIdentifier().toString();
        this.description = "Web Map Tiles"; //$NON-NLS-1$
        this.keywords = new String[]{"Web Map Tiles"};         //$NON-NLS-1$
    }
    
}
