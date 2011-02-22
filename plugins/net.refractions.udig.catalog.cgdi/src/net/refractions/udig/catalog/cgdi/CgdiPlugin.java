package net.refractions.udig.catalog.cgdi;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.refractions.udig.catalog.ICatalog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CgdiPlugin extends Plugin {
    // The shared instance.
    private static CgdiPlugin plugin;
    // Resource bundle.
    private ResourceBundle resourceBundle;
    private ICatalog serviceCatalog;
    private ICatalog layerCatalog;
    /** CgdiPlugin ID field */
    public final static String ID = "net.refractions.udig.catalog.cgdi"; //$NON-NLS-1$
    /**
     * The constructor.
     */
    public CgdiPlugin() {
        super();
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        serviceCatalog = new CGDICatalog();
        layerCatalog = new CGDILayerCatalog();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        resourceBundle = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return x
     */
    public static CgdiPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the shared instance.
     *
     * @return x
     */
    public static ICatalog getDefaultServiceCatalog() {
        return plugin.serviceCatalog;
    }

    /**
     * Returns the shared instance.
     *
     * @return x
     */
    public static ICatalog getDefaultLayerCatalog() {
        return plugin.layerCatalog;
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     *
     * @param key
     * @return x
     */
    public static String getResourceString( String key ) {
        ResourceBundle bundle = CgdiPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     *
     * @return x
     */
    public ResourceBundle getResourceBundle() {
        try {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle
                        .getBundle("net.refractions.udig.catalog.cgdi.CgdiPluginResources"); //$NON-NLS-1$
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
     * </ul>
     * </p>
     *
     * @param message
     * @param t
     */
    public static void log( String message, Throwable t ) {
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        if( message==null )
            message=""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     *
     * <pre><code>
     * private static final String RENDERING = &quot;net.refractions.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *     System.out.println(&quot;your message here&quot;);
     * }
     * </code></pre>
     *
     * </p>
     *
     * @param message
     * @param e
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     *
     * @param trace currently only RENDER is defined
     * @return true if -debug is on for this plugin
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }
}
