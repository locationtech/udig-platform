package net.refractions.udig.style.sld;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * The main plugin class to be used in the desktop.
 */
public class SLDPlugin extends AbstractUdigUIPlugin {

    /** The id of the plug-in */
    public static final String ID = "net.refractions.udig.style.sld"; //$NON-NLS-1$
    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$
	private static SLDPlugin INSTANCE;

    /**
     * The constructor.
     */
    public SLDPlugin() {
        super();
        INSTANCE = this;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log( String message, Throwable e) {
        if( message==null)
            message=""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "net.refractions.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     */
    public static void trace( String message, Throwable e) {
        if( getDefault().isDebugging() ) {            
            if( message != null ) System.out.println( message );
            if( e != null ) e.printStackTrace();
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
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging( final String trace ){
        return getDefault().isDebugging() &&
            "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }

	public static SLDPlugin getDefault() {
		return INSTANCE;
	}

	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}
}