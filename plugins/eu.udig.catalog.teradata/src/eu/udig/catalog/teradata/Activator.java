package eu.udig.catalog.teradata;

import static java.util.Collections.emptyMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.refractions.udig.ui.PlatformGIS;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ucar.unidata.io.RandomAccessFile;
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
		try {
			Class.forName("com.teradata.jdbc.TeraDriver"); //$NON-NLS-1$
			return true;
		} catch (ClassNotFoundException e) {
			PlatformGIS.asyncInDisplayThread(new Runnable() {
				public void run() {
						RandomAccessFile out = null;
						try {
							final String config = "tdgssconfig.jar"; //$NON-NLS-1$
							final String driver = "terajdbc4.jar"; //$NON-NLS-1$
							Bundle teradataLibsBundle = Platform.getBundle("eu.udig.libs.teradata");
							final String dest = FileLocator.toFileURL(FileLocator.find(teradataLibsBundle,new Path("libs"), emptyMap())).getFile();
							
							final String msg = Messages.HostPage_GetDriverMsg;
							Dialog dialog = new Dialog(Display.getCurrent().getActiveShell()) {
								protected org.eclipse.swt.widgets.Control createDialogArea(org.eclipse.swt.widgets.Composite parent) {
									getShell().setText(Messages.HostPage_GetDriverTitle);
									Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
									text.setText(String.format(
											msg, config, driver, dest));
									return text;
								};
								
							};
							if (dialog.open() == Window.CANCEL) return;
							File manifest = new File(dest, ".."+File.separator+"META-INF"+File.separator+"MANIFEST.MF");
							getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, "Updating Manifest: "+manifest, null));
							
							@SuppressWarnings("unchecked")
							List<String> lines = FileUtils.readLines(manifest, "UTF-8");
							for (String string : lines) {
								if(string.contains("com.teradata.jdbc")) {
									return;
								}
							}
							
							
							String manifestData = "Export-Package: org.geotools.data.teradata,"
									+ "\n com.ncr.teradata,\n"
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
	
							
							out = new RandomAccessFile(manifest.getPath(), "rw");
							StringBuilder b= new StringBuilder();
							for (String string : lines) {
								if(b.length() > 0) {
									b.append("\n");
								}
								if(string.trim().startsWith("Export-Package")) {
									b.append(manifestData);
								} else if (string.trim().length() > 0) {
									b.append(string);
								}
							}
							out.seek(0);
							out.write(b.toString().getBytes("UTF-8"));
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
			}, true);
			return false;
		}
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
