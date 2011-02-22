/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.render.internal.feature.basic;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * Plugin class for BasicGridCoverage
 * @author jones
 * @since 0.6.0
 */
public class RendererPlugin extends Plugin {

    private static RendererPlugin plugin;

    public static String ID = "net.refractions.udig.render.feature.basic"; //$NON-NLS-1$
    /**
     * Construct <code>RendererPlugin</code>.
     *
     */
    public RendererPlugin() {
        super();
        plugin=this;
    }

    public static Plugin getDefault(){
        return plugin;
    }
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     * @param message Message to tell the user
     * @param e Throwable assocaited with this message
     */
    public static void log( String message, Throwable e) {
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
     * @param message Message to send to standard out
     * @param e Throwable associated with this trace
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
     * @return true if -debug is used with a .options file to enable tracing
     */
    public static boolean isDebugging( final String trace ){
        boolean on = true; //getDefault().isDebugging();
        boolean enable = "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
        return on && enable;

    }
}
