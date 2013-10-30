/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.browser;

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
