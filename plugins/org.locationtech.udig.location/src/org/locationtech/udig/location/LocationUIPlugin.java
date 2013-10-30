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
package org.locationtech.udig.location;

import org.locationtech.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * The main plugin class to be used in the desktop.
 */
public class LocationUIPlugin extends AbstractUdigUIPlugin {

	private static LocationUIPlugin INSTANCE;
    public static String ID = "org.locationtech.udig.location"; //$NON-NLS-1$
    /** Icons path (value "icons/") */
    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$
    
	/**
	 * The constructor.
	 */
	public LocationUIPlugin() {
		super();
		INSTANCE = this;
	}

	public static LocationUIPlugin getDefault() {
		return INSTANCE;
	}
    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visible ERROR iff:
     * <ul>
     * <li>t is an Exception (we are assuming it is human readable) or if a message is provided
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
     * private static final String RENDERING = &quot;org.locationtech.udig.project/render/trace&quot;;
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

	/* (non-Javadoc)
	 * @see org.locationtech.udig.core.AbstractUdigUIPlugin#getIconPath()
	 */
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}
}
