package net.refractions.udig.validation;

import java.net.URL;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ValidationPlugin extends AbstractUdigUIPlugin {

	private static ValidationPlugin plugin;
	private static final String ID = "net.refractions.udig.validation"; //$NON-NLS-1$
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public ValidationPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
        super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ValidationPlugin getDefault() {
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}
	
    /**
     * Writes an info log in the plugin's log.
     * @param message
     */
    public static void log( String message ) {
        log(message, null);
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e ) {
    	getDefault().getLog()
                .log(new Status(IStatus.INFO, ID, 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
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
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class.  (They must also be part of the .options file) 
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    public static void trace( String traceID, String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (isDebugging(traceID)) {
                if (message != null)
                    System.out.println(message);
                if (e != null)
                    e.printStackTrace();
            }
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
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging() && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$

    }

	@Override
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}

}
