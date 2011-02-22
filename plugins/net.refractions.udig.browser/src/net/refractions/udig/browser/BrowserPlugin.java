package net.refractions.udig.browser;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class BrowserPlugin extends AbstractUIPlugin {
	public static final String ICON_GO = "icons/elcl16/go.png"; //$NON-NLS-1$
    //The shared instance.
	private static BrowserPlugin plugin;

	/**
	 * The constructor.
	 */
	public BrowserPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * @return singlton instance of this
	 */
	public static BrowserPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.refractions.udig.browser", path); //$NON-NLS-1$
	}
}
