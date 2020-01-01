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
package org.locationtech.udig.project.ui.internal.tool.display;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.project.ui.tool.ModalTool;

/**
 * The cursor proxy allows for tool cursor images  to be loaded lazily. It acts as a proxy
 * for cursor image of the tool until the image is really needed to be displayed.
 * 
 * @author Vitalus
 * @since UDIG 1.1
 *
 */
public class CursorProxy {
	
    private volatile Cursor cursor;
    private String imagePath;
    private String hotspotX;
    private String hotspotY;
    private String cursorID;
    private String pluginID;


    /**
     * The constructor to create custom cursor proxy from extention.
     * 
     * @param configuration
     */
    public CursorProxy( IConfigurationElement configuration ) {
        if (configuration != null) {
            imagePath = configuration.getAttribute("image"); //$NON-NLS-1$
            hotspotX = configuration.getAttribute("hotspotX"); //$NON-NLS-1$
            hotspotY = configuration.getAttribute("hotspotY"); //$NON-NLS-1$
            cursorID = configuration.getAttribute("id"); //$NON-NLS-1$
            pluginID = configuration.getNamespace();
        } else {
            cursorID = ModalTool.DEFAULT_CURSOR;
        }
    }
    
    /**
     * Returns cursor ID declared in extention point. 
     * ID is unique in extention registry.
     * 
     * @return
     */
    public String getID(){
    	return cursorID;
    }

    /**
     * @return Returns the SWT cursor object.
     */
    public Cursor getCursor() {
        if (cursor == null) {
            synchronized (this) {
                if (cursor == null) {
                    if (imagePath == null) {
                        cursor = getSystemCursor(cursorID);
                    } else {
                        ImageDescriptor imageDescriptor = AbstractUIPlugin
                                .imageDescriptorFromPlugin(pluginID, imagePath);
                        int x;
                        try {
                            x = Integer.parseInt(hotspotX);
                        } catch (Exception e) {
                            x = 0;
                        }
                        int y;
                        try {
                            y = Integer.parseInt(hotspotY);
                        } catch (Exception e) {
                            y = 0;
                        }
                        if (imageDescriptor == null || imageDescriptor.getImageData() == null)
                            cursor = getSystemCursor(cursorID);
                        else
                            cursor = new Cursor(Display.getDefault(), imageDescriptor
                                    .getImageData(), x, y);
                    }
                }
            }
        }

        return cursor;
    }

    /**
     * Returns system cursor object based on constants from <code>ModalTool</code>
     * interface. These constants are mapped to SWT cursor constants.
     * 
     * @param systemCursorID
     * @return
     */
    static Cursor getSystemCursor( String systemCursorID ) {
    	Display display = PlatformUI.getWorkbench().getDisplay();
        if (systemCursorID == null)
            return display.getSystemCursor(SWT.CURSOR_ARROW);
        
        if (systemCursorID.equals(ModalTool.CROSSHAIR_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_CROSS);
        if (systemCursorID.equals(ModalTool.E_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZEE);
        if (systemCursorID.equals(ModalTool.HAND_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_HAND);
        if (systemCursorID.equals(ModalTool.MOVE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZEALL);
        if (systemCursorID.equals(ModalTool.N_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZEN);
        if (systemCursorID.equals(ModalTool.NE_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZENE);
        if (systemCursorID.equals(ModalTool.NW_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZENW);
        if (systemCursorID.equals(ModalTool.S_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZES);
        if (systemCursorID.equals(ModalTool.SE_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZESE);
        if (systemCursorID.equals(ModalTool.SW_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZESW);
        if (systemCursorID.equals(ModalTool.TEXT_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_IBEAM);
        if (systemCursorID.equals(ModalTool.W_RESIZE_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_SIZESW);
        if (systemCursorID.equals(ModalTool.WAIT_CURSOR))
            return display.getSystemCursor(SWT.CURSOR_WAIT);
        
        if(systemCursorID.equals(ModalTool.NO_CURSOR))
        	return display.getSystemCursor(SWT.CURSOR_NO);
        
        return display.getSystemCursor(SWT.CURSOR_ARROW);
    }

    /**
     * Dispose the cursor.
     */
    public void dispose() {
        if (cursor != null)
            cursor.dispose();
        cursor = null;
    }
}
