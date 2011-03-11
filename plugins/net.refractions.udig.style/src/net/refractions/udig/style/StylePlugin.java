package net.refractions.udig.style;

import net.refractions.udig.core.AbstractUdigUIPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class StylePlugin extends AbstractUdigUIPlugin {
    
    /** The id of the plug-in */
  	public static final String ID = "net.refractions.udig.style"; //$NON-NLS-1$
  	
  	/** Icons path (value "icons/") */
  	public final static String ICONS_PATH = "icons/";//$NON-NLS-1$

	private static StylePlugin INSTANCE;
  	
    /**
     * The constructor.
     */
  	public StylePlugin() {
  	    super();
  	    INSTANCE = this;
  	}
  	
	public IPath getIconPath() {
		return new Path(ICONS_PATH);
	}

	public static StylePlugin getDefault() {
		return INSTANCE;
	}
}