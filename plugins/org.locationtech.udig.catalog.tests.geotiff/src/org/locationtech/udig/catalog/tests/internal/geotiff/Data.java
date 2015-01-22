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
package org.locationtech.udig.catalog.tests.internal.geotiff;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

public final class Data {
    private Data() {
        // No instantiation.
    }

    public static final URL getResource( final Class<?> caller, String name ) throws IOException {
        if (name == null) {
            name = "test-data"; //$NON-NLS-1$
        } else {
            name = "test-data/" + name; //$NON-NLS-1$
        }
        return FileLocator.toFileURL(caller.getResource(name));
    }

}
