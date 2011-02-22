package net.refractions.udig.libs.internal;

import org.eclipse.core.runtime.Platform;
import org.geotools.resources.image.ImageUtilities;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Activator for net.refractions.udig.libs provides global settings
 * to help all the open source projects get along.
 * <p>
 * Currently this activator supplied:
 * <ul>
 * <li>hints about axis order for GeoTools;
 * <li>instructs java not to use native PNG supprt; see UDIG-1391 for details
 * </ul>
 * <p>
 * The contents of this Activator will change over time according to the needs
 * of the libraries and tool kits we are using.
 * </p>
 * @author Jody Garnett
 * @since 1.1.0
 */
public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        // PNG native support is not very good .. this turns it off
        if( Platform.getOS().equals(Platform.OS_WIN32) ){
            ImageUtilities.allowNativeCodec("png", false, false);  //$NON-NLS-1$
        }
    }

    public void stop( BundleContext context ) throws Exception {
    }

}
