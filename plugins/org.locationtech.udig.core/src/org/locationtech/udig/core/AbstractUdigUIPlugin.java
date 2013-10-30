/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.core;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Dynamic ImageRegistry 
 * @author fgdrf
 */
public abstract class AbstractUdigUIPlugin extends AbstractUIPlugin {
    
    /**
     * the default Icon path to access images
     */
    public static final String DEFAULT_ICON_PATH = "icons/";

    /**
     * The constructor.
     */
  	public AbstractUdigUIPlugin() {
  	    super();
  	}
  	
  	public ImageDescriptor getImageDescriptor(String symbolicName) {
  		
  		ImageRegistry imageRegistry = getImageRegistry();
  		ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(symbolicName);
  		if (imageDescriptor == null) {
  			// create it from Path and add it to registry
  			registerImage(getIconPath(), symbolicName);
  		}
  		return imageRegistry.getDescriptor(symbolicName);
  	}

  	public Image getImage(String symbolicName) {
  		ImageRegistry imageRegistry = getImageRegistry();
  		Image image = imageRegistry.get(symbolicName);
  		if (image == null) {
  			// create it from Path and add it to registry
  			registerImage(getIconPath(), symbolicName);
  		}
  		return imageRegistry.get(symbolicName);
  	}
  	
  	private void registerImage(IPath iconPath, String symbolicName) {
  		URL imageUrl = FileLocator.find(getBundle(), iconPath.append(symbolicName), null);
        ImageDescriptor image = ImageDescriptor.createFromURL(imageUrl);
        ImageRegistry imageRegistry = getImageRegistry();
        
        imageRegistry.put(symbolicName, image);
	}
  	
  	/**
     * Returns the shared instance.
     * @return {@link AbstractUdigUIPlugin} singleton
     */
	public abstract IPath getIconPath();
}
