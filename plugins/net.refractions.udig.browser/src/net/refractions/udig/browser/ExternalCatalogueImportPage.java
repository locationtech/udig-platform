package net.refractions.udig.browser;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.browser.LocationListener;

/**
 * Provides the UI and logic required to connect to a catalog.
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public interface ExternalCatalogueImportPage extends IWizardPage {
    /** ExternalCatalogueImportPage XPID field */
    public static final String XPID = 
        "net.refractions.udig.browser.externalCatalogWizard"; //$NON-NLS-1$
    /**
     *
     * @return ID
     */
    public String getID();
    /**
     *
     * @return URL of the catalog
     */
    public URL getURL();
    public void setTitle( String label );
    public void setDescription( String description );
    public void setImageDescriptor( ImageDescriptor descriptionImage );
    /**
     *
     * @param descIcon
     */
    public void setIconDescriptor(ImageDescriptor descIcon);
    /**
     *
     * @return descriptor of the icon image
     */
    public ImageDescriptor getIconDescriptor();
    /**
     *
     * @return LocationListener
     */
    public LocationListener getListener();
    /**
     *
     * @param listen
     */
    public void setListener(LocationListener listen);
    /**
     *
     * @return Name of the view to load browser in.
     */
    public String getViewName();
    /**
     *
     * @param viewName
     */
    public void setViewName(String viewName);
}
