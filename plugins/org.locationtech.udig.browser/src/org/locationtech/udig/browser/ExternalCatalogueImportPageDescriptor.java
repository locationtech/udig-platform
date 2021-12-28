/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.browser;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Describes an a secondary page of the import wizard
 * <p>
 *
 * </p>
 *
 * @author mleslie
 * @since 1.0.0
 */
public class ExternalCatalogueImportPageDescriptor implements ExternalCatalogueImportDescriptor {
    IConfigurationElement element;

    /**
     * @param element
     */
    public ExternalCatalogueImportPageDescriptor(IConfigurationElement element) {
        this.element = element;
    }

    /**
     *
     * @return selected import page
     * @throws CoreException
     */
    public ExternalCatalogueImportPage createImportPage() throws CoreException {
        IConfigurationElement[] childs = element.getChildren("externalCataloguePage"); //$NON-NLS-1$
        ExternalCatalogueImportPage page = (ExternalCatalogueImportPage) childs[0]
                .createExecutableExtension("class"); //$NON-NLS-1$

        page.setTitle(getLabel());
        page.setDescription(getDescription());
        page.setImageDescriptor(getDescriptionImage());
        page.setListener(getListener());
        page.setViewName(getViewName());
        return page;
    }

    @Override
    public String getLabel() {
        if (element.getAttribute("name") == null) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
        return element.getAttribute("name"); //$NON-NLS-1$
    }

    @Override
    public String getViewName() {
        if (element.getAttribute("viewName") == null) { //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        }
        return element.getAttribute("viewName"); //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        String desc = element.getAttribute("description"); //$NON-NLS-1$
        if (desc == null)
            return ""; //$NON-NLS-1$

        return desc.trim();
    }

    @Override
    public String getID() {
        return element.getAttribute("id"); //$NON-NLS-1$
    }

    /**
     *
     * @return descriptor of the banner image
     */
    public ImageDescriptor getImage() {
        String ns = element.getNamespaceIdentifier();
        String banner = element.getAttribute("image"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

    @Override
    public ImageDescriptor getIcon() {
        String ns = element.getNamespaceIdentifier();
        String banner = element.getAttribute("icon"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

    @Override
    public ImageDescriptor getDescriptionImage() {
        String ns = element.getNamespaceIdentifier();
        String banner = element.getAttribute("banner"); //$NON-NLS-1$

        if (banner == null)
            return null;

        return AbstractUIPlugin.imageDescriptorFromPlugin(ns, banner);
    }

    /**
     *
     * @return Service type
     */
    public String getServiceType() {
        return element.getAttribute("type"); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof ExternalCatalogueImportPageDescriptor) {
            ExternalCatalogueImportPageDescriptor descriptor = (ExternalCatalogueImportPageDescriptor) obj;

            return getID() != null && getID().equals(descriptor.getID());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (getID() == null)
            return "".hashCode(); //$NON-NLS-1$
        return getID().hashCode();
    }

    @Override
    public LocationListener getListener() {
        LocationListener blah = null;
        try {
            blah = (LocationListener) element.createExecutableExtension("listener"); //$NON-NLS-1$
        } catch (CoreException e) {
            //
        }
        return blah;
    }
}
