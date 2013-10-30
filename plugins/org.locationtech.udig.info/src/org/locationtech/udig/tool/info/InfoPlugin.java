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
package org.locationtech.udig.tool.info;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Plugin for UDIG Information facilities.
 * <p>
 * Information services is provided by:
 * <ul>
 * <li>InfoTool - a modal tool for the Map editor
 * <li>InfoView - a view used to display the results of the last information request
 * <li>InfoDisplay - allows tool to display new types of content
 * </ul>
 * </p>
 * <p>
 * Programatic access to the current information is not currently provided as part of the public
 * API. If you are interested in this please let us know and it can be moved over.
 * </p>
 */
public class InfoPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static InfoPlugin plugin;

    public static final String ID = "org.locationtech.udig.info"; //$NON-NLS-1$

    public static final String IMG_OBJ_PATH = "icons/obj16/"; //$NON-NLS-1$
    public static final String IMG_OBJ_BASE = IMG_OBJ_PATH + "base_doc_obj.png"; //$NON-NLS-1$
    public static final String IMG_OBJ_FILE = IMG_OBJ_PATH + "file_doc_obj.png"; //$NON-NLS-1$
    public static final String IMG_OBJ_LINK = IMG_OBJ_PATH + "link_doc_obj.png"; //$NON-NLS-1$
    public static final String IMG_OBJ_ACTION = IMG_OBJ_PATH + "action_doc_obj.png"; //$NON-NLS-1$
    
    public static final String IMG_OVR_PATH = "icons/ovr16/"; //$NON-NLS-1$
    public static final String IMG_OVR_ATTACHMENT = IMG_OVR_PATH + "attach_ovr.png"; //$NON-NLS-1$
    public static final String IMG_OVR_TEMPLATE = IMG_OVR_PATH + "template_ovr.png"; //$NON-NLS-1$
    public static final String IMG_OVR_HOTLINK = IMG_OVR_PATH + "hotlink_ovr.png"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public InfoPlugin() {
        super();
        plugin = this;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Access shared InfoPlugin instance.
     * 
     * @return Shared Instance
     */
    public static InfoPlugin getDefault() {
        return plugin;
    }

    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     */
    public static void log(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }

    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     */
    public static void trace(String message, Throwable e) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }

    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     * 
     * @param trace currently only RENDER is defined
     */
    public static boolean isDebugging(final String trace) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);

        addImage(reg, IMG_OBJ_BASE);
        
        addImage(reg, IMG_OBJ_FILE);
        addImage(reg, IMG_OBJ_LINK);
        addImage(reg, IMG_OBJ_ACTION);
        
        addImage(reg, IMG_OVR_ATTACHMENT);
        addImage(reg, IMG_OVR_HOTLINK);
        addImage(reg, IMG_OVR_TEMPLATE);
        
    }
    
    /**
     * Adds image to the registry.
     * 
     * @param reg
     * @param imagePath
     */
    private void addImage(ImageRegistry reg, String imagePath) {
        
        final Bundle bundle = Platform.getBundle(ID);
        
        final Path attachImgPath = new Path(imagePath);
        URL url = FileLocator.find(bundle, attachImgPath, null);
        ImageDescriptor attachImg;
        if( url != null ){
            attachImg = ImageDescriptor.createFromURL(url);            
        }
        else {
            log("Unable to find image for "+imagePath, null);
            attachImg = ImageDescriptor.getMissingImageDescriptor();
        }
        reg.put(imagePath, attachImg);
    }    
}
