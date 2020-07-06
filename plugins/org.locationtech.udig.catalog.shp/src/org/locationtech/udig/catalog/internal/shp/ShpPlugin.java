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
package org.locationtech.udig.catalog.internal.shp;

import java.nio.charset.Charset;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.util.logging.Logging;
import org.locationtech.udig.catalog.shp.preferences.PreferenceConstants;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ShpPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static ShpPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

    public static final String SHP_TRACE_FINEST = "org.locationtech.udig.catalog.shp/debug/finest"; //$NON-NLS-1$
    public static final String SHP_TRACE_FINE = "org.locationtech.udig.catalog.shp/debug/fine"; //$NON-NLS-1$
    
	
    public static final String ID = "org.locationtech.udig.catalog.shp"; //$NON-NLS-1$
	/**
	 * The constructor.
	 */
	public ShpPlugin() {
		super();
		plugin = this;
	}

	/**
     * This method is called upon plug-in activation
     */
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        Logger logger = Logging.getLogger("org.geotools.data.shapefile");
        //Logger logger = ShapefileDataStoreFactory.LOGGER; gt-11 it is not visible
        
        if (ShpPlugin.isDebugging(SHP_TRACE_FINEST) || ShpPlugin.isDebugging(SHP_TRACE_FINE)) {
            if( ShpPlugin.isDebugging(SHP_TRACE_FINE)){
                logger.setLevel(Level.FINE);
            }else{
                logger.setLevel(Level.FINEST);
            }
        } else {
            logger.setLevel(Level.SEVERE);
        }
        logger.addHandler(new ConsoleHandler());
    }

	/**
     * This method is called when the plug-in is stopped
     */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		resourceBundle = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return x
	 */
	public static ShpPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key x
	 * @return x
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ShpPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * @return x
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.locationtech.udig.catalog.internal.shp.ShpPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
    public static void log( String message, Throwable t ) {
        int status = t instanceof Exception || message != null ? IStatus.ERROR : IStatus.WARNING;
        getDefault().getLog().log(new Status(status, ID, IStatus.OK, message, t));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = &quot;org.locationtech.udig.project/render/trace&quot;;
     * if (ProjectUIPlugin.getDefault().isDebugging()
     *      &amp;&amp; &quot;true&quot;.equalsIgnoreCase(RENDERING)) {
     *  System.out.println(&quot;your message here&quot;);
     * }
     * 
     */
    public static void trace(String message, Throwable e) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
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
     * @param trace
     *            currently only RENDER is defined
     */
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
    /**
     * Returns true if spatial indexes will be created by default.
     */
    public boolean isUseSpatialIndex() {
        return getPreferenceStore().getBoolean(PreferenceConstants.P_CREATE_INDEX);
    }

    /**
     * Sets the default behaviour of creating spatial indices for shapefiles.
     *
     * @param useSpatialIndex
     */
    public void setUseSpatialIndex( boolean useSpatialIndex ) {
        getPreferenceStore().setValue(PreferenceConstants.P_CREATE_INDEX, useSpatialIndex);
    }

    /**
     * The value is set according to the system parameter "shp.encoding" if not present or not valid
     * then charset is obtained from UI plugin charset value
     *  
     * @return the charset to be used as String
     */
    public String defaultCharset() {
        String charsetName = System.getProperty(ShpServiceExtension.SHP_CHARSET_PARAM_NAME);
        if (charsetName != null) {
            try {
                //test that Charset actually exists
                return Charset.forName(charsetName).name();
            } catch (Exception e) {
            	getDefault().getLog().log(new Status(IStatus.WARNING, ID, 
            			"Unable to parse charset " + charsetName + ". Default UI charset will be used for shp encoding"));
            }               
        }
        return UiPlugin.getDefault().getPreferenceStore().getString(
        		org.locationtech.udig.ui.preferences.PreferenceConstants.P_DEFAULT_CHARSET);
    }

}
