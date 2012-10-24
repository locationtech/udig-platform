/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
