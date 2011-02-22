package net.refractions.udig.style;

import java.net.URL;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class StylePlugin extends AbstractUIPlugin {

    /** The id of the plug-in */
  	public static final String ID = "net.refractions.udig.style"; //$NON-NLS-1$

  	/** Icons path (value "icons/") */
  	public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

  	/** The shared instance **/
  	private static StylePlugin plugin;

    /** Managed Images instance */
    private Images images = new Images();

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
        final URL iconsUrl = context.getBundle().getEntry( ICONS_PATH );
        images.initializeImages( iconsUrl, getImageRegistry() );
    }

  	/**
     * Returns the shared instance.
     * @return StylePlugin singleton
     */
  	public static StylePlugin getDefault() {
  	    return plugin;
  	}

    /**
     * Images instance for use with ImageConstants.
     *
     * @return Images for use with ImageConstants.
     */
    public Images getImages() {
        return images;
    }

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
