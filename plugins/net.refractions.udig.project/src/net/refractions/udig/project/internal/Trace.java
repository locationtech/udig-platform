package net.refractions.udig.project.internal;

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
    /** Trace rendering such as RenderExecutor and RenderManager and RenderImpl */
    public static final String RENDER = "net.refractions.udig.project/debug/render/trace"; //$NON-NLS-1$
    /** Trace the execution of commands */
    public static final String COMMANDS = "net.refractions.udig.project/debug/commands/trace"; //$NON-NLS-1$
    public static final String MODEL = "net.refractions.udig.project/debug/model/trace"; //$NON-NLS-1$
}
