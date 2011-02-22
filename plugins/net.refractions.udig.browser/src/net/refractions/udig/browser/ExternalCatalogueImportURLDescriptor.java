/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.browser;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationListener;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public class ExternalCatalogueImportURLDescriptor
        implements ExternalCatalogueImportDescriptor {
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

    public ImageDescriptor getIcon() {
        return icon;
    }

    public String getDescription() {
        return (this.description == null) ? "" : this.description; //$NON-NLS-1$
    }

    public ImageDescriptor getDescriptionImage() {
        return this.descImage;
    }

    /**
     *
     * @param attribute
     */
    public void setDescription( String attribute ) {
        this.description = attribute;
    }

    /**
     *
     * @param descriptor
     */
    public void setDescriptionImage( ImageDescriptor descriptor ) {
        this.descImage = descriptor;
    }

    /**
     *
     * @param descriptor
     */
    public void setIcon( ImageDescriptor descriptor ) {
        this.icon = descriptor;
    }

    /**
     *
     * @param name
     */
    public void setListener(String name) {
        LocationListener blah = null;
        if(name != null) {
            try {
                blah = (LocationListener)Class.forName(name).newInstance();
            } catch (InstantiationException e) {
                //
            } catch (IllegalAccessException e) {
                //
            } catch (ClassNotFoundException e) {
                //
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

    public LocationListener getListener() {
        return this.listener;
   }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return this.viewName;
    }
}
