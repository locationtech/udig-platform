package net.refractions.udig.catalog.rasterings;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class RasteringsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static RasteringsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public RasteringsPlugin() {
		super();
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

		plugin = null;
		this.resourceBundle = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return Default instance of RasteringsPlugin.
	 */
	public static RasteringsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key
	 * @return Value of the indicated resource string.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = RasteringsPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * @return This plugins ResourceBundle.
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
				this.resourceBundle = ResourceBundle.getBundle("net.refractions.udig.catalog.rasterings.RasteringsPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.refractions.udig.catalog.rasterings", path); //$NON-NLS-1$
	}

    public static void log( String message2, Throwable t ) {
        String message=message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, getDefault().getBundle().getSymbolicName(), IStatus.OK, message, t));
    }

}
