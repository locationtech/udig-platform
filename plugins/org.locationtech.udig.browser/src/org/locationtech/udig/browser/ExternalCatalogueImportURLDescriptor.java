/** uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.browser;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationListener;

/**
 * TODO Purpose of this class
 *
 * @author mleslie
 * @since 1.0.0
 */
public class ExternalCatalogueImportURLDescriptor implements ExternalCatalogueImportDescriptor {
    private URL url;

    private String description;

    private ImageDescriptor descImage;

    private ImageDescriptor icon;

    private String label;

    private String id;

    private LocationListener listener;

    private String viewName;

    /**
     * @param url1
     *
     */
    public ExternalCatalogueImportURLDescriptor(URL url1) {
        super();
        this.url = url1;
    }

    /**
     *
     * @return URL of the catalog
     */
    public URL getUrl() {
        return this.url;
    }

    @Override
    public String getID() {
        return this.id;
    }

    /**
     *
     * @param id
     */
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String getLabel() {
        return (this.label == null) ? "" : this.label; //$NON-NLS-1$
    }

    /**
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public ImageDescriptor getIcon() {
        return icon;
    }

    @Override
    public String getDescription() {
        return (this.description == null) ? "" : this.description; //$NON-NLS-1$
    }

    @Override
    public ImageDescriptor getDescriptionImage() {
        return this.descImage;
    }

    /**
     *
     * @param attribute
     */
    public void setDescription(String attribute) {
        this.description = attribute;
    }

    /**
     *
     * @param descriptor
     */
    public void setDescriptionImage(ImageDescriptor descriptor) {
        this.descImage = descriptor;
    }

    /**
     *
     * @param descriptor
     */
    public void setIcon(ImageDescriptor descriptor) {
        this.icon = descriptor;
    }

    /**
     *
     * @param name
     */
    public void setListener(String name) {
        LocationListener blah = null;
        if (name != null) {
            try {
                blah = (LocationListener) Class.forName(name).getDeclaredConstructor()
                        .newInstance();
            } catch (Exception e) {
                // do nothing
            }
        }
        this.listener = blah;
    }

    /**
     *
     * @param listener
     */
    public void setListener(LocationListener listener) {
        this.listener = listener;
    }

    @Override
    public LocationListener getListener() {
        return this.listener;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public String getViewName() {
        return this.viewName;
    }
}
