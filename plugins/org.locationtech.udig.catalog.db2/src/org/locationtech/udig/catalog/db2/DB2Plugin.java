/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.db2;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DB2Plugin extends AbstractUIPlugin {

    /** id of plugin, matches id declared in plugin.xml * */
    public static final String ID = "org.locationtech.udig.catalog.db2"; //$NON-NLS-1$

    // The shared instance.
    private static DB2Plugin plugin;

    /**
     * The constructor.
     */
    public DB2Plugin() {
        super();
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try{
            Thread.currentThread().setContextClassLoader(DB2NGDataStoreFactory.class.getClassLoader());
            Logger.getLogger("org.geotools.data.db2").setLevel(Level.SEVERE); //$NON-NLS-1$
        }finally{
            Thread.currentThread().setContextClassLoader(loader);
        }

    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static DB2Plugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor( String path ) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.locationtech.udig.catalog.db2", path); //$NON-NLS-1$
    }

    /**
     * Logs the Throwable in the plugin's log.
     * <p>
     * This will be a user visable ERROR iff:
     * <ul>
     * <li>t is an Exception we are assuming it is human readable or if a message is provided
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
     * 
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
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
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
}
