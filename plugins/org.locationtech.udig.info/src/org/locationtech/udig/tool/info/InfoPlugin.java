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
package org.locationtech.udig.tool.info;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.osgi.framework.BundleContext;

/**
 * Plugin for UDIG Information facilities.
 * <p>
 * Information services is provided by:
 * <ul>
 * <li>InfoTool - a modal tool for the Map editor
 * <li>InfoView - a view used to display the results of the last information request
 * <li>InfoDisplay - allows tool to display new types of content
 * </ul>
 * </p>
 * <p>
 * Programatic access to the current information is not currently provided as part of the public
 * API. If you are interested in this please let us know and it can be moved over.
 * </p>
 */
public class InfoPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static InfoPlugin plugin;

    public static final String ID = "org.locationtech.udig.info"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public InfoPlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Access shared InfoPlugin instance.
     *
     * @return Shared Instance
     */
    public static InfoPlugin getDefault() {
        return plugin;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     *
     * @deprecated Use LoggerSupport
     */
    public static void log(String message, Throwable e) {
        LoggingSupport.log(getDefault(), message, e);
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     *
     * @deprecated Use
     *             {@link LoggingSupport#trace(org.eclipse.core.runtime.Plugin, String, Throwable)}
     *             instead.
     */
    public static void trace(String message, Throwable e) {
        LoggingSupport.trace(getDefault(), message, e);
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
}
