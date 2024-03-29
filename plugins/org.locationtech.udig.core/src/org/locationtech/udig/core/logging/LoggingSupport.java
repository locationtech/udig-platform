/**
 * uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2022, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.core.logging;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.locationtech.udig.core.internal.CorePlugin;

/**
 * To allow logging in the same way and with the same behavior overall modules this provides access
 * to Plug-In Logging capability.
 *
 * In addition for developer purposes it allows you to trace messages, in case of Debugging-Mode for
 * Plug-In is enabled (see {@link Plugin#isDebugging()}).
 *
 * @author Frank Gasdorf (fgdrf)
 *
 */
public class LoggingSupport {

    private LoggingSupport() {
        // just private
    }

    /**
     * @param plugin Plug-In to create log messages for
     * @param status Status to log
     */
    public static void log(Plugin plugin, IStatus status) {
        if (plugin != null) {
            ILog log = plugin.getLog();
            if (log != null) {
                log.log(status);
            }
        }
    }

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

        int status;
        if (e == null) {
            status = IStatus.INFO;
        } else {
            if (e instanceof Exception) {
                status = IStatus.ERROR;
            } else {
                status = IStatus.WARNING;
            }
        }

        if (!(status == IStatus.INFO && StringUtils.isEmpty(message))) {
            log(logPlugin, new Status(status, logPlugin.getBundle().getSymbolicName(), message, e));
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
     * Logs the given throwable to the platform log, indicating the class and method from where it
     * is being logged (this is not necessarily where it occurred).
     *
     * This convenience method is for internal use by the Workbench only and must not be called
     * outside the Workbench.
     *
     * @param plugin Plug-In to create log messages for
     * @param clazz The calling class.
     * @param methodName The calling method name.
     * @param t The throwable from where the problem actually occurred.
     */
    public static void log(Plugin plugin, Class<?> clazz, String methodName, Throwable t) {
        final String msg = MessageFormat.format("Exception in {0}.{1}: {2}", //$NON-NLS-1$
                clazz.getName(), methodName, t);
        log(plugin, msg, t);
    }

    /**
     * Messages that only engage if is debugging is enabled for the given Plug-In.
     * <p>
     * <code>
     * CorePlugin.getDefault().isDebugging();
     * </code>
     *
     * @param plugin Plug-In is used to check, if debugging is enabled. Only if debugging is enabled
     *        the message & throwable are written to standard output (console) if present
     * @param message message or just null, in case of writing just {@link Throwable} to standard
     *        output
     * @param e {@link Throwable} if <b>not</b> <code>null</code> will be written to standard output
     */
    public static void trace(Plugin plugin, String message, Throwable e) {
        if (plugin != null && plugin.isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }

    /**
     * Adds the name of the caller class to the message.
     *
     * @param plugin Plug-In to create log messages for
     * @param caller class of the object doing the trace.
     * @param message tracing message, may be null.
     * @param e exception, may be null.
     */
    public static void trace(Plugin plugin, Class<?> caller, String message, Throwable e) {
        if (caller != null) {
            trace(plugin, caller.getSimpleName() + ": " + message, e); //$NON-NLS-1$
        }
    }

    /**
     * Messages that only engage if getDefault().isDebugging() and the trace option traceID is true.
     * Available trace options can be found in the Trace class. (They must also be part of the
     * .options file)
     *
     * @param plugin Plug-In to create log messages for
     * @param traceID option as string to check if debugging is enabled, see
     *        {@link #isDebugging(Plugin, String)}
     * @param caller class of the object doing the trace.
     * @param message tracing message, may be null.
     * @param e exception, may be null.
     */
    public static void trace(Plugin plugin, String traceID, Class<?> caller, String message,
            Throwable e) {
        if (isDebugging(plugin, traceID)) {
            trace(plugin, traceID, caller, message, e);
        }
    }

    /**
     * Performs the Platform.getDebugOption to check on the provided trace
     * <p>
     * Note: <code>plugin.isDebugging()<code> must also be on.
     * </p>
     *
     * @param plugin Plug-In to check if trace is set to true a debugging is enabled.
     * @param trace the trace option, e.g. "RENDER"
     */
    public static boolean isDebugging(final Plugin plugin, final String trace) {
        return plugin != null && plugin.isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$
    }

}
