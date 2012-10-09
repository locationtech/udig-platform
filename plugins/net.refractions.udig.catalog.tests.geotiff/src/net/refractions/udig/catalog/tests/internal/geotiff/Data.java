/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.tests.internal.geotiff;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

public final class Data {
    private Data() {
        // No instantiation.
    }

    public static final URL getResource( final Class caller, String name ) throws IOException {
        if (name == null) {
            name = "test-data"; //$NON-NLS-1$
        } else {
            name = "test-data/" + name; //$NON-NLS-1$
        }
        return FileLocator.toFileURL(caller.getResource(name));
    }

}
