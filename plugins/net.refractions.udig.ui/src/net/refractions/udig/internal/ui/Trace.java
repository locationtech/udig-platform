package net.refractions.udig.internal.ui;

/**
* Constants for use with eclipse tracing api.
* Rember only engage tracing if WMSPlugin.getDefault().isDebugging().
* <p>
* Sample use:<pre><code>
* static import net.refractions.udig.project.ui.internal.RENDERING;
* 
* if( UiPlugin.isDebugging( RENDERING ) ){
*      System.out.println( "your message here" );
* }
* </code></pre>
* </p>
*/
public interface Trace {
    /** You may set this to "true" in your .options file */
    public static final String DND =
        "net.refractions.udig.ui/debug/dnd"; //$NON-NLS-1$    
    /** traces the locking/unlocking of the UDIGDisplaySafeLoc */
    public static final String UDIG_DISPLAY_SAFE_LOCK =
        "net.refractions.udig.ui/debug/udigdisplaysafelock"; //$NON-NLS-1$    
    public static final String FEATURE_TABLE = "net.refractions.udig.ui/debug/featuretable"; //$NON-NLS-1$
}