package net.refractions.udig.catalog.internal.wms;

/**
 * Constants for use with eclipse tracing api.
 * Rember only engage tracing if WMSPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:<pre><code>
 * static import net.refractions.udig.project.ui.internal.RENDERING;
 * 
 * if( WmsPlugin.isDebugging( RENDERING ) ){
 *      System.out.println( "your message here" );
 * }
 * </code></pre>
 * </p>
 */
public interface Trace {
    /** You may set this to "true" in your .options file */
    public static final String REQUEST =
        "net.refractions.udig.catalog.wms/debug/request"; //$NON-NLS-1$    
}