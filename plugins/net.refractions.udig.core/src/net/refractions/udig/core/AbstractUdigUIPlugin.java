/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
