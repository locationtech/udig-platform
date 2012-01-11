package net.crischan.udig.arcgrid;

import java.io.IOException;

import net.crischan.udig.arcgrid.internal.Messages;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;

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