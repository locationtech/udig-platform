package eu.udig.catalog.csw;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {
	public static final String ID = "eu.udig.catalog.csw"; //$NON-NLS-1$
	private static Activator plugin;

	public static Activator getDefault() {
		return plugin;
	}
	public Activator() {
		plugin = this;
	}
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
	}
	
    public static void log( String message, Throwable t ) {
        String msg = message == null ? "" : message;
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, msg, t));
    }


}
