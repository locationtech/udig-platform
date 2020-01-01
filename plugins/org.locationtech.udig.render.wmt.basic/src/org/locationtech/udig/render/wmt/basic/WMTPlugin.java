/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.wmt.basic;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.ows.wms.WebMapServer;
import org.osgi.framework.BundleContext;

/**
 * WMTRenderer plugin
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTPlugin extends AbstractUIPlugin {

    public static final String ID = "org.locationtech.udig.render.wmt.basic"; //$NON-NLS-1$
    private static WMTPlugin plugin;

    /**
     * Construct <code>WMSPlugin</code>.
     *
     */
    public WMTPlugin() {
        super();
        plugin=this;
    }

    public static WMTPlugin getDefault(){
        return plugin;
    }
    
    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(WebMapServer.class.getClassLoader());
            Logger logger = Logger.getLogger("org.geotools.data.ows");//$NON-NLS-1$
            if (!isDebugging()) {
                logger.setLevel(Level.SEVERE);
            } else {
                logger.setLevel(Level.FINEST);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
    
    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
        plugin = null;
    }
    
    public static void log(String message) {
        log(message, null);
    }
    
    public static void log(String message, Throwable exception) {
        WMTPlugin.getDefault().getLog().log(
                new Status(IStatus.INFO, WMTPlugin.ID, IStatus.OK, 
                        message, exception));
    }    
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:<pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     */
    public final static void trace( String message, Throwable e) {
        if( getDefault().isDebugging() ) {
            if( message != null ) System.out.println( message );
            if( e != null ) e.printStackTrace();
        }
    }
    public static final void trace( String message ) {
        trace( message, null );
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
    
    public static void debug(String message, final String trace) {
        debug(message, null, trace);
    }
    
    public static void debug(String message, Throwable e, final String trace) {
        if(isDebugging(trace)) {
            if(message != null) System.out.println(message);
            if(e != null) e.printStackTrace();
        }
    }
}
