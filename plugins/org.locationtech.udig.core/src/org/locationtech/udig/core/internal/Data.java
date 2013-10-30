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
package org.locationtech.udig.core.internal;

// J2SE dependencies
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Utility methods for dealing with class data, included with jar.
 * <p>
 * Data should be located in a folder with a dash in the name, similar to the javadoc "doc-files"
 * convention. This ensures that data directories don't look anything like normal java packages.
 * </p>
 * <p>
 * Example:
 * 
 * <pre><code>
 * class MyClass {
 *     public ImageDescriptor example() {
 *         return ImageDescriptor.create(TestData.url(this, &quot;test-data/test.png&quot;)).getImage();
 *     }
 * }
 * </code></pre>
 * 
 * Where:
 * <ul>
 * <li>MyClass.java
 * <li>
 * <li>test-data/test.png</li>
 * </ul>
 * </ul>
 * </p>
 * <p>
 * By convention you should try and locate data near the class that uses it.
 * </p>
 * 
 * @author Jody Garnett
 * @since 0.7.0
 */
public class Data {
    /**
     * Provided a {@link BufferedReader} for named test data. It is the caller responsability to
     * close this reader after usage.
     * 
     * @param caller The class of the object associated with named data.
     * @param name of test data to load.
     * @return The reader, or <code>null</code> if the named test data are not found.
     * @throws IOException if an error occurs during an input operation.
     */
    public static final BufferedReader reader( final Class caller, final String name )
            throws IOException {
        final URL url = url(caller, name);
        if (url == null) {
            return null; // echo handling of getResource( ... )
        }
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    /**
     * Provided a {@link BufferedReader} for named test data. It is the caller responsability to
     * close this reader after usage.
     * 
     * @param host Object associated with named data
     * @param name of test data to load
     * @return The reader, or <code>null</code> if the named test data are not found.
     * @throws IOException if an error occurs during an input operation.
     */
    public static final BufferedReader reader( final Object host, final String name )
            throws IOException {
        return reader(host.getClass(), name);
    }

    /**
     * Locate named test-data resource for caller.
     * 
     * @param caller Class used to locate test-data.
     * @param name name of test-data.
     * @return URL or null of named test-data could not be found.
     * @todo Should this be getURL() - or simply url? I tend to save getX method for accessors.
     */
    public static final URL url( final Class caller, String name ) {
        return caller.getResource(name);
    }

    /**
     * Locate named test-data resource for caller.
     * 
     * @param caller Object used to locate test-data
     * @param name name of test-data
     * @return URL or null of named test-data could not be found
     * @todo Should this be getURL() - or simply url? I tend to save getX method for accessors.
     */
    public static final URL url( final Object caller, final String name ) {
        return url(caller.getClass(), name);
    }

    /**
     * Access to <code>url(caller, path)</code> as a {@link File}.
     * <p>
     * 
     * <pre><code>
     * Data.file(this, null)
     * </code></pre>
     * 
     * </p>
     * 
     * @param caller Calling object used to locate data
     * @param path Path to file in testdata
     * @return File
     * @throws IOException if the file is not found.
     */
    public static final File file( final Object caller, final String path ) throws IOException {
        final URL url = url(caller, path);
        if (url != null) {
            // Based SVGTest
            final File file = new File(URLDecoder.decode(url.getFile(), "UTF-8")); //$NON-NLS-1$
            if (file.exists()) {
                return file;
            }
        }
        throw new FileNotFoundException("Could not locate:" + path); //$NON-NLS-1$
    }
    /**
     * Access to <code>url(caller, path)</code> as a {@link File}.
     * <p>
     * 
     * <pre><code>
     * Data.file(this, null)
     * </code></pre>
     * 
     * </p>
     * 
     * @param caller Calling class used to locate data
     * @param path Path to file in testdata
     * @return File
     * @throws IOException if the file is not found.
     */
    public static final File file( Class caller, final String path ) throws IOException {
        final URL url = url(caller, path);
        if (url != null) {
            // Based SVGTest
            final File file = new File(URLDecoder.decode(url.getFile(), "UTF-8")); //$NON-NLS-1$
            if (file.exists()) {
                return file;
            }
        }
        throw new FileNotFoundException("Could not locate:" + path); //$NON-NLS-1$
    }

    /**
     * Creates a temporary file with the given name.
     * 
     * @param caller
     * @param name
     * @return File if available for name
     * @throws IOException
     */
    public static final File temp( final Object caller, final String name ) throws IOException {
        File testData = file(caller, null);
        int split = name.lastIndexOf('.');
        String prefix = split == -1 ? name : name.substring(0, split);
        String suffix = split == -1 ? null : name.substring(split + 1);
        File tmp = File.createTempFile(prefix, suffix, testData);
        tmp.deleteOnExit();
        return tmp;
    }
}
