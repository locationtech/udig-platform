/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.osgi.framework.BundleContext;

/**
 * PlugIn for org.locationtech.udig.core, used by utility classes to access workbench log.
 *
 * @author jones
 * @since 0.3
 */
public class CorePlugin extends Plugin {

    private static CorePlugin plugin;

    /**
     * A url stream handler that delegates to the default one but if it doesn't work then it returns
     * null as the stream.
     */
    public static final URLStreamHandler RELAXED_HANDLER = new URLStreamHandler() {

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            try {
                URL url = new URL(u.toString());
                return url.openConnection();
            } catch (MalformedURLException e) {
                return null;
            }
        }
    };

    /**
     * creates a plugin instance
     */
    public CorePlugin() {
        super();
        plugin = this;
    }

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * Create a URL from the provided spec; willing to create a URL even if the spec does not have a
     * registered handler. Can be used to create "jdbc" URLs for example.
     *
     * @param spec
     * @return URL if possible
     * @throws RuntimeException of a MalformedURLException resulted
     */
    public static URL createSafeURL(String spec) {
        try {
            return new URL(null, spec, RELAXED_HANDLER);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a URI from the provided spec; willing to create a URI even if the spec does not have a
     * registered handler. Can be used to create "jdbc" URLs for example.
     *
     * @param spec
     * @return URI if possible
     * @throws RuntimeException of a URISyntaxException resulted
     */
    public static URI createSafeURI(String spec) {
        try {
            return new URI(spec);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the system created plugin object
     *
     * @return the plugin object
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    /**
     * Takes a string, and splits it on '\n' and calls stringsToURLs(String[])
     */
    public static List<URL> stringsToURLs(String string) {
        String[] strings = string.split("\n"); //$NON-NLS-1$

        return stringsToURLs(strings);
    }

    /**
     * Converts each element of an array from a String to a URL. If the String is not a valid URL,
     * it attempts to load it as a File and then convert it. If that fails, it ignores it. It will
     * not insert a null into the returning List, so the size of the List may be smaller than the
     * size of <code>strings</code>
     *
     * @param strings an array of strings, each to be converted to a URL
     * @return a List of URLs, in the same order as the array
     */
    public static List<URL> stringsToURLs(String[] strings) {
        List<URL> urls = new ArrayList<>();

        for (String string : strings) {
            try {
                urls.add(new URL(string));
            } catch (MalformedURLException e) {
                // not a URL, maybe it is a file
                try {
                    urls.add(new File(string).toURI().toURL());
                } catch (MalformedURLException e1) {
                    // Not a URL, not a File. nothing to do now.
                }
            }
        }
        return urls;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     *
     * @deprecated Use {@link LoggingSupport#log(Plugin, String, Throwable)} instead.
     */
    public static void log(String message2, Throwable e) {
        LoggingSupport.log(getDefault(), message2, e);
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much preferred to do this:
     *
     * <pre>
     * <code> private static final String RENDERING =
     * &quot;org.locationtech.udig.project/render/trace&quot;; if
     * (ProjectUIPlugin.getDefault().isDebugging() &amp;&amp;
     * &quot;true&quot;.equalsIgnoreCase(RENDERING)) { System.out.println(&quot;your message
     * here&quot;); }
     *
     * @deprecated Use {@link LoggingSupport#trace(Plugin, String, Throwable)} instead.
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

    public static boolean isDeveloping() {
        return System.getProperty("UDIG_DEVELOPING") != null; //$NON-NLS-1$
    }
}
