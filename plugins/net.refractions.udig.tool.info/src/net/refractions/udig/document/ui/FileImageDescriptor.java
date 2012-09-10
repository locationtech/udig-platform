/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.document.ui;

import net.refractions.udig.tool.info.InfoPlugin;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Image overlay for attachment documents.
 * 
 * @author Naz Chan 
 */
public class FileImageDescriptor extends CompositeImageDescriptor {

    private ImageDescriptor baseImg = null;
    
    private Point size;
    private boolean isAttachment = false;
    
    private static final int OVERLAY_X_POS = 0;
    private static final int OVERLAY_Y_POS = 0;
    
    public FileImageDescriptor() {
        baseImg = PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
        final Rectangle baseImgBounds = baseImg.createImage().getBounds();
        size = new Point(baseImgBounds.width, baseImgBounds.height);
    }
    
    /**
     * Creates a file image icon. This method has the option to put an attachment overlay on the
     * icon.
     * 
     * @param isAttachment
     * @return
     */
    public Image createFileImage(boolean isAttachment) {
        this.isAttachment = isAttachment;
        return createImage();
    }
    
    @Override
    protected void drawCompositeImage(int width, int height) {

        drawImage(baseImg.getImageData(), 0, 0);

        if (isAttachment) {
            final ImageDescriptor overlayImageDescriptor = InfoPlugin.getDefault()
                    .getImageRegistry().getDescriptor(InfoPlugin.IMG_OVR_ATTACHMENT);
            if (overlayImageDescriptor != null) {
                drawImage(overlayImageDescriptor.getImageData(), OVERLAY_X_POS, OVERLAY_Y_POS);
            }
        }
        
    }

    @Override
    protected Point getSize() {
        return size;
    }
    
}
