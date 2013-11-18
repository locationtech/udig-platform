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
package org.locationtech.udig.arcgrid;

import java.io.IOException;

import org.locationtech.udig.arcgrid.internal.Messages;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;
import org.locationtech.udig.catalog.rasterings.AbstractRasterService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;


public class ArcGridGeoResourceImplementation extends AbstractRasterGeoResource {
	public ArcGridGeoResourceImplementation(AbstractRasterService service, String name) {
		super(service, name);
	}

	protected AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
	    if( monitor == null ) monitor = new NullProgressMonitor();
	    
		monitor.beginTask(Messages.ArcGridGeoResourceImplementation_Connecting, 2);
		try {
		    monitor.worked(1);	
		    return new AbstractRasterGeoResourceInfo(this, "asc", "grd");  //$NON-NLS-1$//$NON-NLS-2$
		}
		finally {
            monitor.done();
		}
	}
	
	public ArcGridServiceImplementation getService(){
	    return (ArcGridServiceImplementation) service;
	}
}
