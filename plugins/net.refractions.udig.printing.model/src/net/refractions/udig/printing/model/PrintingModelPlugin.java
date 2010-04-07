package net.refractions.udig.printing.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class PrintingModelPlugin extends Plugin {
	//The shared instance.
	private static PrintingModelPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
    public static final String BOX_PRINTER_EXTENSION_ID = "net.refractions.udig.printing.ui.boxprinter"; //$NON-NLS-1$
    public static final String EDIT_ACTION_EXTENSION_ID = "net.refractions.udig.printing.ui.editAction"; //$NON-NLS-1$
	
	/**
	 * Plugin ID
	 */
    public static final String ID = "net.refractions.udig.printing.model"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public PrintingModelPlugin() {
		super();
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
		resourceBundle = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return the PrintingModelPlugin instance
	 */
	public static PrintingModelPlugin getDefault() {
		return plugin;
	}

	/**
	 * @param key string used to look up the resource
	 * @return the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PrintingModelPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle
	 * @return Returns the plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("net.refractions.udig.printing.model.PrintingModelPluginResources"); //$NON-NLS-1$
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
     * @param message 
     * @param t 
     */
    public static void log( String message, Throwable t ) {
        if (message==null )
            message=""; //$NON-NLS-1$
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     * </code></pre>
     * </p>
     * @param message 
     * @param e 
     */
    public static void trace( String message, Throwable e) {
        if( getDefault().isDebugging() ) {
            if( message != null ) System.out.println( "[[PrintingModelPlugin-trace]]:: "+message ); //$NON-NLS-1$
            if( e != null ) e.printStackTrace();
        }
    }
    
    public static void trace(String message) {
    	trace(message, null);
    }
    
    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p> 
     * @param trace currently only RENDER is defined
     * @return true if -debug is on for this plugin 
     */
    public static boolean isDebugging( final String trace ){
        return getDefault().isDebugging() &&
            "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
}
