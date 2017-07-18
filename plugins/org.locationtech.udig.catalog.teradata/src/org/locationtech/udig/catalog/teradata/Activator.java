/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.teradata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.catalog.teradata.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ucar.unidata.io.RandomAccessFile;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	private static final String GEOTOOLS_LIBS_PLUGIN = "org.locationtech.udig.libs.teradata";
	private static final String LICENSE_PLUGIN_ID = "org.locationtech.udig.libs.teradata.license_10.0";
	static final String CONFIG_FILE_NAME = "tdgssconfig.jar"; //$NON-NLS-1$
	static final String JDBC_FILE_NAME = "terajdbc4.jar"; //$NON-NLS-1$

	private static final class LicenseDialog extends Dialog {
		private String path = System.getProperty("user.home");

		private LicenseDialog(Shell parentShell) {
			super(parentShell);
		}

		public File getPath() {
			return new File(path);
		}
		protected int getShellStyle() {
			return SWT.RESIZE | SWT.MAX |super.getShellStyle();
		}
		String htmlForm = "<html><head><style type='text/css'>body {font-family: Arial,Helvetica,sans-serif;font-size: 12px;background: #ffffff;}</style></head><body>%s</body</html>";
		protected org.eclipse.swt.widgets.Control createDialogArea(
				final org.eclipse.swt.widgets.Composite parent) {
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));

			getShell().setText(Messages.GetDriverTitle);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.verticalSpacing = 0;
			comp.setLayout(layout);
			Control msg;
		    try {
				Browser browser = new Browser(comp, SWT.NONE);
				browser.setText(String.format(htmlForm,Messages.GetHTMLDriverMsg));
		        msg = browser;
		        browser.addLocationListener(new LocationListener() {
					
					@Override
					public void changing(LocationEvent event) {
						event.doit = false;
						org.eclipse.swt.program.Program.launch(event.location);
						
					}
					
					@Override
					public void changed(LocationEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
		    } catch (SWTError e) {
				Text text = new Text(comp, SWT.MULTI
						| SWT.READ_ONLY | SWT.WRAP | SWT.BORDER
						| SWT.SHADOW_IN);
				text.setText(Messages.GetDriverMsg);
				msg = text;
		    }
			GridDataFactory.fillDefaults().span(2, 1)
					.hint(500, 200).applyTo(msg);
			area(path, JDBC_FILE_NAME, comp);

			return comp;
		}

		public Text area(String defaultPath,
				final String textData, final Composite comp) {
			final Text text1 = new Text(comp, SWT.SINGLE
					| SWT.BORDER | SWT.SHADOW_IN);
			text1.setText(defaultPath);
			text1.addListener(SWT.Modify, new Listener() {

				@Override
				public void handleEvent(Event event) {
					path = text1.getText(); 
					updateRestart();
				}

			});
			GridDataFactory.swtDefaults()
					.align(SWT.FILL, SWT.CENTER)
					.grab(true, false).applyTo(text1);
			Button button1 = new Button(comp, SWT.PUSH);
			button1.setText("...");
			GridDataFactory.swtDefaults()
					.align(SWT.END, SWT.CENTER)
					.applyTo(button1);
			button1.addListener(SWT.Selection,
					new Listener() {

						@Override
						public void handleEvent(Event event) {
							DirectoryDialog fdialog = new DirectoryDialog(
									comp.getShell(),
									SWT.OPEN);
							String result = fdialog.open();
							if (result != null) {
								text1.setText(result);
							}

						}
					});
			return text1;
		}

		@Override
		protected Button createButton(Composite parent,
				int id, String label, boolean defaultButton) {
			Button button = super.createButton(parent, id,
					label, defaultButton);
			if (id == Window.OK) {
				button.setText("Restart");
				button.setEnabled(false);
			}
			return button;
		}

		private void updateRestart() {
			File dir = new File(path.trim());
			boolean driv = false;
			boolean conf = false;
			if(dir.exists() && dir.isDirectory()) {
				for (File f : dir.listFiles()) {
					if(f.getName().equals(JDBC_FILE_NAME)) {
						driv = true;
					} else if(f.getName().equals(CONFIG_FILE_NAME)){
						conf = true;
					}
					if(driv && conf) break;
				}
			}
			getButton(Window.OK).setEnabled(conf && driv);
		}
	}

	// The plug-in ID
	public static final String PLUGIN_ID = "org.locationtech.udig.catalog.teradata"; //$NON-NLS-1$

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
		if(TeradataServiceExtension.getFactory().isAvailable()) {
			return true;
		} else {
			PlatformGIS.asyncInDisplayThread(new Runnable() {
				public void run() {
					final Shell shell = Display.getCurrent().getActiveShell();
					try {
						final String pluginName = LICENSE_PLUGIN_ID;
						final File pluginsDir = findPluginsDir();

						LicenseDialog dialog = new LicenseDialog(shell);

						if (dialog.open() == Window.CANCEL)
							return;

						File newPlugin;
						boolean needUserCopy;
						
						File requiredPluginDir = new File(pluginsDir, pluginName);
						try {
							newPlugin = requiredPluginDir;
							createPluginStructure(newPlugin);
							needUserCopy = false;
						} catch (Exception e ) {
							newPlugin = new File(System.getProperty("user.home"),pluginName);
							createPluginStructure(newPlugin);
							needUserCopy = true;
						}

						final String dest = new File(newPlugin,"libs").getPath();
						final File finalNewPlugin = newPlugin;
						
						transfer(dest, JDBC_FILE_NAME, dialog);
						transfer(dest, CONFIG_FILE_NAME, dialog);

						if(needUserCopy) {
							while(!requiredPluginDir.exists()) {
								Dialog dialog2 = new Dialog(shell) {
									protected Control createDialogArea(Composite parent) {
										Control comp;
									    try {
								    	Browser browser = new Browser(parent, SWT.NONE);

								        String mainMessage = String.format(Messages.GetHTMLCopyPluginMsg,finalNewPlugin.getParentFile().getAbsolutePath(), finalNewPlugin.getAbsolutePath(), pluginsDir.getAbsolutePath());
										browser.setText(mainMessage);
										browser.addLocationListener(new LocationListener() {
											
											@Override
											public void changing(LocationEvent event) {
												event.doit = false;
												org.eclipse.swt.program.Program.launch(event.location);												
											}
											
											@Override
											public void changed(LocationEvent event) {
											}
										});
										comp = browser;
									    } catch (SWTError e) {
											Text text = new Text(parent, SWT.MULTI
													| SWT.READ_ONLY | SWT.WRAP | SWT.BORDER
													| SWT.SHADOW_IN);

											String mainMessage = String.format(Messages.GetDriverMsg,finalNewPlugin.getAbsolutePath(), pluginsDir.getAbsolutePath());
											text.setText(mainMessage);
											comp = text;
									    }
									    GridDataFactory.fillDefaults().hint(500, 200).applyTo(comp);

									    return comp;
									};
									@Override
									protected Button createButton(Composite parent,
											int id, String label, boolean defaultButton) {
										Button button = super.createButton(parent, id,label, defaultButton);
										if (id == Window.OK) button.setText("Restart");
	
										return button;
									}
								};
								
								if(dialog2.open() == Window.CANCEL) return;
							}
						}
						
						String cmd = buildCommandLine(shell);
						if (cmd != null) {
							System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
							System.setProperty(PROP_EXIT_DATA, cmd);
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().restart();
							//PlatformUI.getWorkbench().restart();
						}

					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}

				public File findPluginsDir() throws IOException {
					Bundle bundle = Platform.getBundle(GEOTOOLS_LIBS_PLUGIN);
					
					String filePath = FileLocator.toFileURL(FileLocator.find(bundle, new Path("glib"), new HashMap<String, String>())).getFile();
					return new File(filePath).getParentFile().getParentFile();
				}

				private void transfer(String dest, String driver, LicenseDialog dialog)
						throws IOException {
					RandomAccessFile out = null;
					RandomAccessFile in = null;
					try {
						String fromPath = new File(dialog.getPath(),driver).getPath();
						
						String toPath = new File(dest, driver).getPath();
						out = new RandomAccessFile(toPath, "rw");
						in = new RandomAccessFile(fromPath.trim(), "r");

						out.seek(0);
						byte[] cs = new byte[(int) in.length()];
						in.readFully(cs);
						out.write(cs);
					} finally {
						if (out != null)
							try {
								out.close();
							} catch (IOException e1) {
								throw new RuntimeException(e1);
							}
						if (in != null)
							try {
								in.close();
							} catch (IOException e1) {
								throw new RuntimeException(e1);
							}
					}

				}
			}, true);
			return false;
		}
	}

	protected static void createPluginStructure(File newPlugin) throws IOException {
		Bundle teradataLibsBundle = Platform.getBundle(PLUGIN_ID);
		URL manifest = FileLocator.toFileURL(FileLocator.find(teradataLibsBundle, new Path("License-MANIFEST.MF"), new HashMap<String, String>()));
		File libsDir = new File(newPlugin, "libs");
		libsDir.mkdirs();
		File metaInf = new File(libsDir.getParentFile(), "META-INF");
		FileUtils.copyURLToFile(manifest, new File(metaInf,"MANIFEST.MF"));
	}

	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

	private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$

	private static final String CMD_DATA = "-clean"; //$NON-NLS-1$

	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	/**
	 * Create and return a string with command line options for eclipse.exe that
	 * will launch a new workbench that is the same as the currently running
	 * one, but using the argument directory as its workspace.
	 * 
	 * @param workspace
	 *            the directory to use as the new workspace
	 * @return a string of command line options or null on error
	 */
	private static String buildCommandLine(Shell shell) {
		String property = System.getProperty(PROP_VM);
		if (property == null) {
			MessageDialog
					.openError(
							shell,
							"Restart Error",
							"Unable to determine the correct JVM to use.  Please restart manually using the udig-clean script (in the uDig install directory)");
			return null;
		}

		StringBuffer result = new StringBuffer(512);
		result.append(property);
		result.append(NEW_LINE);

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null) {
			result.append(vmargs);
		}
		result.append("-Declipse.refreshBundles=true\n");

		// append the rest of the args, replacing or adding -data as required
		property = System.getProperty(PROP_COMMANDS);
		if (property == null) {
			result.append(CMD_DATA);
			result.append(NEW_LINE);
		} else {
			result.append(property);
			result.append(CMD_DATA);
			result.append(NEW_LINE);
		}

		// put the vmargs back at the very end (the eclipse.commands property
		// already contains the -vm arg)
		if (vmargs != null) {
			result.append(CMD_VMARGS);
			result.append(NEW_LINE);
			result.append(vmargs);
			result.append("-Declipse.refreshBundles=true");
		}

		return result.toString();
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
