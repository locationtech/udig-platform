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