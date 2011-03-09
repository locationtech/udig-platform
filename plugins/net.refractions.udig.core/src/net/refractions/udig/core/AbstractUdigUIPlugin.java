package net.refractions.udig.core;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Dynamic ImageRegistry 
 * @author fgdrf
 */
public abstract class AbstractUdigUIPlugin extends AbstractUIPlugin {

	/** The shared instance **/
  	private static AbstractUdigUIPlugin plugin;
  	
    /**
     * The constructor.
     */
  	public AbstractUdigUIPlugin() {
  	    super();
  	    plugin = this; 
  	}
  	
  	@Override
  	public void start(BundleContext context) throws Exception {
  		super.start(context);
  	}
  	
  	@Override
  	public void stop(BundleContext context) throws Exception {
  		super.stop(context);
  	}

  	public static ImageDescriptor getImageDescriptor(String symbolicName) {
  		ImageRegistry imageRegistry = plugin.getImageRegistry();
  		ImageDescriptor imageDescriptor = imageRegistry.getDescriptor(symbolicName);
  		if (imageDescriptor == null) {
  			// create it from Path and add it to registry
  			registerImage(plugin.getIconPath(), symbolicName);
  		}
  		return imageRegistry.getDescriptor(symbolicName);
  	}

  	public static Image getImage(String symbolicName) {
  		ImageRegistry imageRegistry = plugin.getImageRegistry();
  		Image image = imageRegistry.get(symbolicName);
  		if (image == null) {
  			// create it from Path and add it to registry
  			registerImage(plugin.getIconPath(), symbolicName);
  		}
  		return imageRegistry.get(symbolicName);
  	}
  	
  	private static void registerImage(IPath iconPath, String symbolicName) {
  		URL imageUrl = FileLocator.find(plugin.getBundle(), iconPath.append(symbolicName), null);
        ImageDescriptor image = ImageDescriptor.createFromURL(imageUrl);
        ImageRegistry imageRegistry = plugin.getImageRegistry();
        
        imageRegistry.put(symbolicName, image);
	}

	public abstract IPath getIconPath();
}
