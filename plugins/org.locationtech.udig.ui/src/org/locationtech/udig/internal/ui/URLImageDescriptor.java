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
package org.locationtech.udig.internal.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;

/**
 * An ImageDescriptor that gets its information from a URL. This class is not public API. Use
 * ImageDescriptor#createFromURL to create a descriptor that uses a URL.
 */
public class URLImageDescriptor extends ImageDescriptor {
    private URL url;

    /**
     * Creates a new URLImageDescriptor.
     *
     * @param url The URL to load the image from. Must be non-null.
     */
    public URLImageDescriptor(URL url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof URLImageDescriptor)) {
            return false;
        }
        return ((URLImageDescriptor) o).url.equals(this.url);
    }

    /**
     * Method declared on ImageDesciptor. Returns null if the image data cannot be read.
     */
    @Override
    public ImageData getImageData() {
        ImageData result = null;
        InputStream in = getStream();
        if (in != null) {
            try {
                result = new ImageData(in);
            } catch (SWTException e) {
                if (e.code != SWT.ERROR_INVALID_IMAGE)
                    throw e;
                // fall through otherwise
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    // System.err.println(getClass().getName()+".getImageData(): "+
                    // "Exception while closing InputStream : "+e);
                }
            }
        }
        return result;
    }

    /**
     * Returns a stream on the image contents. Returns null if a stream could not be opened.
     *
     * @return the stream for loading the data
     */
    protected InputStream getStream() {
        try {
            return new BufferedInputStream(url.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    /**
     * The <code>URLImageDescriptor</code> implementation of this <code>Object</code> method returns
     * a string representation of this object which is suitable only for debugging.
     */
    @Override
    public String toString() {
        return "URLImageDescriptor(" + url + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
