/*******************************************************************************
 * Copyright (c) 2010,2012 City of Vienna.
 *
 * This program and the accompanying materials are made available under the            
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0    
 * which accompanies this distribution.                                                
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at                                
 * http://www.eclipse.org/org/documents/edl-v10.php.                                   
 *                                                                                     
 * Contributors:                                                                       
 *    Aritz Davila (Axios) - initial implementation and documentation                  
 *    Mauricio Pazos (Axios) - initial implementation and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.tests;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.locationtech.udig.tools.tests"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
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
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
