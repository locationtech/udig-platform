package net.refractions.udig.project.ui.internal;

/**
 * Constants for use with eclipse tracing api. Rember only engage tracing if
 * ProjectUIPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:
 * 
 * <pre><code>
 *  static import net.refractions.udig.project.ui.internal.RENDERING;
 *  
 *  if( ProjectUIPlugin.isDebugging( RENDERING ) ){
 *       System.out.println( &quot;your message here&quot; );
 *  }
 * </code></pre>
 * 
 * </p>
 */
public interface Trace {
    /**
     * Trace ID to print tracing logs during the rendering process
     */
    public static final String RENDER = "net.refractions.udig.project.ui/debug/render/trace"; //$NON-NLS-1$
    /**
     * Trace ID to print tracing logs during the drag and drop process
     */
    public static final String DND = "net.refractions.udig.project.ui/debug/dnd/trace"; //$NON-NLS-1$
    public static final String VIEWPORT = "net.refractions.udig.project.ui/debug/viewport/trace"; //$NON-NLS-1$
}
