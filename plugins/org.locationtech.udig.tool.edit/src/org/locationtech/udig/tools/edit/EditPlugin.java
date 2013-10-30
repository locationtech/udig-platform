/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class EditPlugin extends AbstractUIPlugin {

	public static final String ID = "org.locationtech.udig.tools.edit"; //$NON-NLS-1$
    public static final String SELECTION = "org.locationtech.udig.tools.edit/selection"; //$NON-NLS-1$
    public static final String ACTIVATOR = "org.locationtech.udig.tools.edit/activator"; //$NON-NLS-1$
    public static final String HANDLER_LOCK = "org.locationtech.udig.tools.edit/handler/lock"; //$NON-NLS-1$
    public static final String BEHAVIOUR = "org.locationtech.udig.tools.edit/behaviour"; //$NON-NLS-1$
    public static final String RUN_ASSERTIONS = "org.locationtech.udig.tools.edit/debug/assertions"; //$NON-NLS-1$
	//The shared instance.
	private static EditPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public EditPlugin() {
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		try{
		}catch (Exception e) {
			log("Error loading messages, check that file exists", e); //$NON-NLS-1$
		}
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
	public static EditPlugin getDefault() {
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
     * <p>
     * This should be used for user level messages.
     * </p>
     * @param message 
     * @param e 
     */
    public static void log( String message2, Throwable e) {
        String message=message2;
    	if( message==null )
    		message=""; //$NON-NLS-1$
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message+"\n"); //$NON-NLS-1$
            if (e != null)
                e.printStackTrace(System.out);
        }
    }
    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class.  (They must also be part of the .options file) 
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    public static void trace( String traceID, String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (isDebugging(traceID)) {
                if (message != null)
                    System.out.println(message+"\n"); //$NON-NLS-1$
                if (e != null)
                    e.printStackTrace(System.out);
            }
        }
    }
    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * @return true if this plugig is in your .options file 
     */
    public static boolean isDebugging( final String trace ){
        return getDefault().isDebugging() && (trace  == null ||
            "true".equalsIgnoreCase(Platform.getDebugOption(trace))); //$NON-NLS-1$    
    }    
}
