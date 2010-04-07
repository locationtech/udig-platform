package net.refractions.udig.browser;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationListener;

/**
 * Describes a catalog to connect to.
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public interface ExternalCatalogueImportDescriptor {
    /**
     *
     * @return ID
     */
    public String getID();
    /**
     *
     * @return Short description
     */
    public String getLabel();
    /**
     *
     * @return descriptor of icon image
     */
    public ImageDescriptor getIcon();
    /**
     *
     * @return Long description
     */
    public String getDescription();
    /**
     *
     * @return descriptor of banner image
     */
    public ImageDescriptor getDescriptionImage(); 
    /**
     * @return LocationListener
     * 
     */
    public LocationListener getListener();
    /**
     *
     * @return secondary name of the view to create the browser in.
     */
    public String getViewName();
}