/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.internal.ui;

import java.io.IOException;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.ui.ImageCache;
import net.refractions.udig.ui.OverlayCache;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

/**
 * A full decorator for IService, provides the default appearance.
 * <p>
 * This is a full decorator, plug-in must handle image support.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Obtain ISerivce.getMetadata().getIcon() if available
 * <li>Supply a default based on ISerivce.getMetadata().getType()
 * </ul>
 * </p>
 * <p>
 * To use this decorator as per normal:
 * 
 * <pre><code>
 *  ILabelProvider lp = ... basic label provider
 *  ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
 *  viewer.setLabelProvider( new DecoratingLabelProvider( lp, decorator ));
 * </code></pre>
 * 
 * </p>
 * 
 * @author jgarnett
 * @since 0.3
 */
public class ServiceDecorator implements ILabelDecorator {

    protected ImageCache imageCache;
    protected OverlayCache overlayCache;

    public ServiceDecorator() {
        overlayCache = new OverlayCache();
        imageCache = new ImageCache();
    }

    /**
     * Returns an image that is based on the given image, but decorated with additional information
     * relating to the state of the provided element. Text and image decoration updates can occur as
     * a result of other updates within the workbench including deferred decoration by background
     * processes. Clients should handle labelProviderChangedEvents for the given element to get the
     * complete decoration.
     * 
     * @see LabelProviderChangedEvent
     * @see IBaseLabelProvider#addListener
     * @param image the input image to decorate, or <code>null</code> if the element has no image
     * @param element the element whose image is being decorated
     * @return the decorated image, or <code>null</code> if no decoration is to be applied
     * @see org.eclipse.jface.resource.CompositeImageDescriptor
     */
    public Image decorateImage( Image image, Object element ) {
        if (!(element instanceof IService)) {
            return null;
        }
        IService service = (IService) element;
        if (image == null) {
            image = baseIcon(service);
        }
        switch( service.getStatus() ) {
        case CONNECTED:
            return overlayCache.applyStatus(image, Images
                    .getDescriptor(ImageConstants.CONNECTED_OVR));

        case NOTCONNECTED:
            return overlayCache.applyStatus(image, Images.getDescriptor(ImageConstants.WAIT_OVR));

        case BROKEN:
            return overlayCache.applyStatus(image, Images.getDescriptor(ImageConstants.ERROR_OVR));

        default:
            return image;
        }
    }

    Image baseIcon( IService service ) {
        ImageDescriptor icon = null;
        try {
            icon = service.getInfo(null).getImageDescriptor();
        } catch (IOException e) {
            CatalogUIPlugin.log(null, e);
        }
        if (icon == null)
            return CatalogUIPlugin.image(service);

        return imageCache.getImage(icon);
    }

    /**
     * TODO summary sentence for decorateText ...
     * 
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String,
     *      java.lang.Object)
     * @param text
     * @param element
     * @return the decorated text label, or <code>null</code> if no decoration is to be applied
     */
    public String decorateText( String text, Object element ) {
        return null;
    }

    /**
     * TODO summary sentence for addListener ...
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @param listener
     */
    public void addListener( ILabelProviderListener listener ) {
        // we are not playing with events at this time
    }

    /**
     * TODO summary sentence for dispose ...
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        overlayCache.disposeAll();
        imageCache.dispose();
    }

    /**
     * TODO summary sentence for isLabelProperty ...
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     * @param element
     * @param property
     * @return
     */
    public boolean isLabelProperty( Object element, String property ) {
        return element instanceof IService
                && ("status".equals(property) || "metadata".equals(property)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * TODO summary sentence for removeListener ...
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @param listener
     */
    public void removeListener( ILabelProviderListener listener ) {
        // we are not playing with events at this time
    }

}
