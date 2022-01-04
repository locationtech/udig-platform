/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.ui;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IProcess;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveChangeListener;
import org.locationtech.udig.catalog.IResolveFolder;
import org.locationtech.udig.catalog.ISearch;
import org.locationtech.udig.catalog.IService;

/**
 * Label generation for IResolve (must be non-blocking and quick).
 * <p>
 * Compare and contrast with ResovleLabelDecorator which is allowed to block.
 * </p>
 *
 * @author jgarnett
 * @since 0.7.0
 */
public class ResolveLabelProviderSimple extends LabelProvider implements IResolveChangeListener {

    public ResolveLabelProviderSimple() {
        CatalogPlugin.addListener(this);
    }

    @Override
    public void changed(final IResolveChangeEvent event) {
        if (event.getType() != IResolveChangeEvent.Type.POST_CHANGE)
            return;

        final IResolve resolve = event.getResolve();
        if (resolve == null)
            return;

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                fireLabelProviderChanged(
                        new LabelProviderChangedEvent(ResolveLabelProviderSimple.this, resolve));
            }
        });
    }

    /**
     * Generate text from the resolve.getURI()
     * <p>
     * Note this name is only used as a first try, the ResolveLabelDecorator is expected to provide
     * a label based on Name or Title information.
     * </p>
     *
     * @param element
     * @return label based on IResolve.getIdentifier
     */
    @Override
    public String getText(Object element) {
        if (element instanceof IResolve) {
            IResolve resolve = (IResolve) element;
            try {
                if (resolve instanceof IGeoResource) {
                    IGeoResource resource = (IGeoResource) resolve;
                    String title = resource.getTitle();
                    ID id = resource.getID();
                    if (title == null) {
                        title = id.labelResource();
                    }
                    return title;

                } else if (resolve instanceof IService) {
                    IService service = (IService) resolve;
                    ID id = service.getID();

                    String title = service.getTitle();
                    if (title == null) {
                        // we are going to fake something here
                        String name = id.toString();
                        name = name.replace('_', ' ');
                        name = name.replace("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
                        return name;
                    }
                    if (id.getTypeQualifier() != null) {
                        return title + "(" + id.getTypeQualifier() + ")";
                    } else {
                        return title;
                    }
                } else if (resolve instanceof IProcess) {
                    IProcess proc = (IProcess) element;
                    return proc.getInfo(new NullProgressMonitor()).getTitle();
                } else if (resolve instanceof ISearch) {
                    ISearch search = (ISearch) element;
                    return search.getInfo(new NullProgressMonitor()).getTitle();
                } else if (resolve instanceof IResolveFolder) {
                    IResolveFolder folder = (IResolveFolder) element;
                    return folder.getID().toString();
                } else {
                    return resolve.getID().toString();
                }
            } catch (IOException e) {
                CatalogUIPlugin.trace("Error fetching the Title for the resource", e); //$NON-NLS-1$
            }
        }
        return super.getText(element);
    }

    /**
     * Obtain image for the provided element.
     * <p>
     * To accomplish this quickly we simply make use of constants from CatalogUIPlugin. We need a
     * second pass that makes use of the real icon from the real resource.
     * </p>
     *
     * @param element is expected to be IResolve
     * @return the image used to label the element, or <code>null</code> if there is no image for
     *         the given object
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof IResolve) {
            return CatalogUIPlugin.image((IResolve) element);
        }
        return super.getImage(element);
    }
}
