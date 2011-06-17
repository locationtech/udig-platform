package eu.udig.catalog.teradata;

import java.io.File;
import java.net.URL;

import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.teradata.TeradataDataStoreFactory;
import org.osgi.framework.BundleContext;

import eu.udig.catalog.teradata.internal.Messages;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "eu.udig.catalog.teradata"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static boolean checkTeradataDrivers() {
		final boolean[] returnVal = new boolean[1];
		PlatformGIS.asyncInDisplayThread(new Runnable() {
			public void run() {
				try {
					Class.forName("com.teradata.jdbc.TeraDriver"); //$NON-NLS-1$
					returnVal[0] = true;
				} catch (ClassNotFoundException e) {
					returnVal[0] = false;
					String config = "tdgssconfig.jar"; //$NON-NLS-1$
					String driver = "terajdbc4.jar"; //$NON-NLS-1$
					Location install = Platform.getInstallLocation();
					String toPlugin = File.separatorChar+"plugins"+File.separatorChar+"eu.udig.libs.teradata"+File.separatorChar+"libs";
					String dest;
					if(install == null || install.getURL() == null) {
						dest = "<uDigInstall>"+toPlugin;
					} else {
						dest = install.getURL().getFile()+toPlugin;
					}
					String msg = Messages.HostPage_GetDriverMsg;
					MessageDialog.openInformation(Display.getCurrent()
							.getActiveShell(),
							Messages.HostPage_GetDriverTitle, String.format(
									msg, config, driver, dest));

				}
			}
		}, true);
		return returnVal[0];
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static void log(String message, Throwable t) {
		int status = t instanceof Exception || message != null ? IStatus.ERROR
				: IStatus.WARNING;
		getDefault().getLog().log(
				new Status(status, PLUGIN_ID, IStatus.OK, message, t));
	}
}
