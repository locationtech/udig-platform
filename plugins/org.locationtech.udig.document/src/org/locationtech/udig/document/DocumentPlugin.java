/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.document.ui.IDocumentImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DocumentPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.document"; //$NON-NLS-1$

    // The shared instance
    private static DocumentPlugin plugin;

    /**
     * The constructor
     */
    public DocumentPlugin() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static DocumentPlugin getDefault() {
        return plugin;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, 0, message, e));
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);

        addImage(reg, IDocumentImages.IMG_OBJ_BASE);

        addImage(reg, IDocumentImages.IMG_OBJ_FILE);
        addImage(reg, IDocumentImages.IMG_OBJ_LINK);
        addImage(reg, IDocumentImages.IMG_OBJ_ACTION);

        addImage(reg, IDocumentImages.IMG_OVR_ATTACHMENT);
        addImage(reg, IDocumentImages.IMG_OVR_HOTLINK);
        addImage(reg, IDocumentImages.IMG_OVR_TEMPLATE);

    }

    /**
     * Adds image to the registry.
     *
     * @param reg
     * @param imagePath
     */
    private void addImage(ImageRegistry reg, String imagePath) {

        final Bundle bundle = Platform.getBundle(PLUGIN_ID);

        final Path attachImgPath = new Path(imagePath);
        URL url = FileLocator.find(bundle, attachImgPath, null);
        ImageDescriptor attachImg;
        if (url != null) {
            attachImg = ImageDescriptor.createFromURL(url);
        } else {
            log("Unable to find image for " + imagePath, null);
            attachImg = ImageDescriptor.getMissingImageDescriptor();
        }
        reg.put(imagePath, attachImg);
    }
}
