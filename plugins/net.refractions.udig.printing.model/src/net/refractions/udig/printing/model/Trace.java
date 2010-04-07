package net.refractions.udig.printing.model;

/**
 * Constants for use with eclipse tracing api.
 * Rember only engage tracing if PrintingModelPlugin.getDefault().isDebugging().
 * <p>
 * Sample use:<pre><code>
 * static import net.refractions.udig.project.ui.internal.RENDERING;
 * 
 * if( WMSPlugin.isDebugging( RENDERING ) ){
 *      System.out.println( "your message here" );
 * }
 * </code></pre>
 * </p>
 */
public interface Trace {
    /** You may set this to "true" in your .options file */
    public static final String PRINTING =
        "net.refractions.udig.printing.model/debug/printing/trace"; //$NON-NLS-1$    
}