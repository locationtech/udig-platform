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
