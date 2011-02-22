package net.refractions.udig.location;

import java.net.URL;

import net.refractions.udig.location.internal.Images;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LocationUIPlugin extends AbstractUIPlugin {

    public static String ID = "net.refractions.udig.location"; //$NON-NLS-1$
    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

	//The shared instance.
	private static LocationUIPlugin plugin;
    private Images images = new Images();

	/**
	 * The constructor.
	 */
	public LocationUIPlugin() {
		plugin = this;
	}

    /**
     * Set up shared images.
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        final URL iconsUrl = context.getBundle().getEntry(ICONS_PATH);

        images.initializeImages(iconsUrl, getImageRegistry());
    }
    /**
     * Cleanup after shared images.
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void stop( BundleContext context ) throws Exception {
        images.cleanUp();
        super.stop(context);
    }

	/**
	 * Returns the shared instance.
	 */
	public static LocationUIPlugin getDefault() {
		return plugin;
	}

    public Images getImages() {
        return images;
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
        if (message == null)
            message = ""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
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
    public static void trace( String message ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
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
