package net.refractions.udig.style;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;

public class StylePlugin extends AbstractUdigUIPlugin {
    
    /** The id of the plug-in */
  	public static final String ID = "net.refractions.udig.style"; //$NON-NLS-1$
  	
  	/** Icons path (value "icons/") */
  	public final static String ICONS_PATH = "icons/";//$NON-NLS-1$
  	
  	/** The shared instance **/
  	private static StylePlugin plugin;
  	
    /**
     * The constructor.
     */
  	public StylePlugin() {
  	    super();
  	    plugin = this; 
  	}
  	
  	/**
     * Set up shared images.
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @param context
     * @throws Exception
     */
    public void start(BundleContext context) throws Exception {
        super.start( context );
    }
    
  	/**
     * Returns the shared instance.
     * @return StylePlugin singleton
     */
  	public static StylePlugin getDefault() {
  	    return plugin;
  	}
    
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	@Override
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}
}