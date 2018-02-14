package org.locationtech.udig.tool.measure.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MeasurementToolPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.tool.measure"; //$NON-NLS-1$

    // The shared instance
    private static MeasurementToolPlugin plugin;

    /**
     * The constructor
     */
    public MeasurementToolPlugin() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MeasurementToolPlugin getDefault() {
        return plugin;
    }

    public static void log(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, 0, message, e));
    }

}
