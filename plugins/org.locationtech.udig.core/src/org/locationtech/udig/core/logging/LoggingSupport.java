/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2021, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.core.logging;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.locationtech.udig.core.internal.CorePlugin;

/**
 * To allow logging in the same way and with the same behaviour overall modules this provides access
 * to Plug-In Logging capability.
 *
 * In addition for developer purposes it allows you to trace messages, in case of Debugging-Mode for
 * Plug-In is enabled (see {@link Plugin#isDebugging()}).
 *
 * @author Frank Gasdorf (fgdrf)
 *
 */
public class LoggingSupport {

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     *
     * @param plugin Plug-In to create log messages for
     * @param logMessage message or just null, in case of Just logging throwable/exception
     * @param e {@link Throwable} will be logged as {@link IStatus#WARNING}, {@link Exception} is
     *        logged as {@link IStatus#ERROR}, if <code>null</code> that just {@link IStatus#INFO}
     */
    public static void log(Plugin plugin, String logMessage, Throwable e) {
        String message = logMessage;

        Plugin logPlugin = (plugin == null ? CorePlugin.getDefault() : plugin);

        ILog pluginLog = logPlugin.getLog();

        int status = (e == null ? IStatus.INFO
                : (e instanceof Exception ? IStatus.ERROR : IStatus.WARNING));

        if (!(status == IStatus.INFO && StringUtils.isEmpty(message))) {
            pluginLog.log(new Status(status, logPlugin.getBundle().getSymbolicName(), message, e));
        }
    }

    /**
     * See {@link #log(Plugin, String, Throwable)} just without an explicit logMessage.
     */
    public static void log(Plugin plugin, Throwable e) {
        log(plugin, null, e);
    }

    /**
     * See {@link #log(Plugin, String, Throwable)} just without an explicit {@link Throwable}
     */
    public static void log(Plugin plugin, String logMessage) {
        log(plugin, logMessage, null);
    }

    /**
     * Messages that only engage if is debugging is enabled for the given Plug-In.
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code>
     * private static final String RENDERING = &quot;org.locationtech.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *     System.out.println(&quot;your message here&quot;);
     * }
     * </code>
     */
    public static void trace(Plugin plugin, String message, Throwable e) {
        if (plugin.isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
}
