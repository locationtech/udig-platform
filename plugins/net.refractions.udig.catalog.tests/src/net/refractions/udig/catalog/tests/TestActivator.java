package net.refractions.udig.catalog.tests;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.refractions.udig.catalog.CatalogPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.junit.Ignore;
import org.osgi.framework.BundleContext;

@Ignore
public class TestActivator extends Plugin {
    public static final String ID = "net.refractions.udig.catalog.tests"; //$NON-NLS-1$

    // The shared instance.
    private static TestActivator plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public TestActivator() {
        super();
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
    }
    /**
     * Cleanup after shared images.
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void stop( BundleContext context ) throws Exception {
        
        super.stop(context);

        plugin = null;
        resourceBundle = null;
    }
    /**
     * Returns the shared instance.
     */
    public static TestActivator getDefault() {
        
        return plugin;
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString( String key ) {
        ResourceBundle bundle = CatalogPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle
                        .getBundle("net.refractions.udig.catalog.tests.TestActivatorResources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
        return resourceBundle;
    }
    
    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
     */
    public static void log( String message2, Throwable t ) {
        String message=message2;
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }
 
}
