/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation;

import org.locationtech.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ValidationPlugin extends AbstractUdigUIPlugin {

    private static final String ID = "org.locationtech.udig.validation"; //$NON-NLS-1$

    public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

    private static ValidationPlugin INSTANCE;

    /**
     * The constructor.
     */
    public ValidationPlugin() {
        super();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        INSTANCE = null;
    }
    /**
     * Writes an info log in the plugin's log.
     * 
     * @param message
     */
    public static void log(String message) {
        log(message, null);
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log(String message, Throwable e) {
        getDefault().getLog().log(
                new Status(IStatus.INFO, ID, 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    public static void trace(String message, Throwable e) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }

    public static ValidationPlugin getDefault() {
        return INSTANCE;
    }

    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class. (They must also be part of the
     * .options file)
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * 
     */
    public static void trace(String traceID, String message, Throwable e) {
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
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.core.AbstractUdigUIPlugin#getIconPath()
     */
    public IPath getIconPath() {
        return new Path(ICONS_PATH);
    }

}
