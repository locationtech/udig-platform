package eu.udig.catalog.teradata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
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
					String toPlugin = File.separator + "plugins"
							+ File.separator + "eu.udig.libs.teradata"
							+ File.separator + "libs";
					String dest;
					if (install == null || install.getURL() == null) {
						dest = "<uDigInstall>" + toPlugin;
					} else {
						dest = install.getURL().getFile() + toPlugin;
					}
					dest = dest.replace(File.separator + File.separator,
							File.separator);
					String msg = Messages.HostPage_GetDriverMsg;
					MessageDialog.openInformation(Display.getCurrent()
							.getActiveShell(),
							Messages.HostPage_GetDriverTitle, String.format(
									msg, config, driver, dest));

					BufferedWriter out = null;
					try {
						//File manifest = new File("/Users/jeichar/Project/udig-platform/plugins/eu.udig.libs.teradata/META-INF/MANIFEST.MF");
						File manifest = new File(dest + File.separator + ".."
								+ File.separator + "META-INF" + File.separator
								+ "MANIFEST.MF");
						getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, "Updating Manifest: "+manifest, null));
						
						@SuppressWarnings("unchecked")
						List<String> lines = FileUtils.readLines(manifest, "UTF-8");
						String manifestData = ",\n com.ncr.teradata,\n"
								+ " com.teradata.jdbc,\n"
								+ " com.teradata.jdbc.interfaces,\n"
								+ " com.teradata.jdbc.jdbc,\n"
								+ " com.teradata.jdbc.jdbc.console,\n"
								+ " com.teradata.jdbc.jdbc.fastexport,\n"
								+ " com.teradata.jdbc.jdbc.fastload,\n"
								+ " com.teradata.jdbc.jdbc.monitor,\n"
								+ " com.teradata.jdbc.jdbc.raw,\n"
								+ " com.teradata.jdbc.jdbc_3,\n"
								+ " com.teradata.jdbc.jdbc_3.dbmetadata,\n"
								+ " com.teradata.jdbc.jdbc_3.ifjdbc_4,\n"
								+ " com.teradata.jdbc.jdbc_3.util,\n"
								+ " com.teradata.jdbc.jdbc_4,\n"
								+ " com.teradata.jdbc.jdbc_4.ifsupport,\n"
								+ " com.teradata.jdbc.jdbc_4.io,\n"
								+ " com.teradata.jdbc.jdbc_4.logging,\n"
								+ " com.teradata.jdbc.jdbc_4.parcel,\n"
								+ " com.teradata.jdbc.jdbc_4.statemachine,\n"
								+ " com.teradata.jdbc.jdbc_4.util,\n"
								+ " com.teradata.jdbc.resource,\n"
								+ " com.teradata.tdgss.jalgapi,\n"
								+ " com.teradata.tdgss.jgssp2gss,\n"
								+ " com.teradata.tdgss.jgssp2ldap,\n"
								+ " com.teradata.tdgss.jgssp2td1,\n"
								+ " com.teradata.tdgss.jgssp2td2,\n"
								+ " com.teradata.tdgss.jgssspi,\n"
								+ " com.teradata.tdgss.jtdgss";

						out = new BufferedWriter(new FileWriter(new File("/tmp/manifest.mf")));
						StringBuilder b= new StringBuilder();
						for (String string : lines) {
							if(b.length() > 0) {
								b.append("\n");
							}
							if(string.trim().length() > 0) {
								b.append(string);
							}
						}
						b.append(manifestData);
						out.write(b.toString());
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					} finally {
						if (out != null)
							try {
								out.close();
							} catch (IOException e1) {
								throw new RuntimeException(e1);
							}
					}

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
