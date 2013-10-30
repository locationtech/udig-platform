/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.refractions.udig.internal.ui.operations;

import java.util.HashMap;
import java.util.Iterator;

import net.refractions.udig.ui.internal.Messages;
import net.refractions.udig.ui.operations.OpAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.dialogs.DialogUtil;

/**
 * Provides labels for view children.
 */
public class OperationLabelProvider extends LabelProvider {
    private HashMap images;

    Image cacheImage(ImageDescriptor desc) {
        if (images == null)
            images = new HashMap(21);
        Image image = (Image) images.get(desc);
        if (image == null) {
            image = desc.createImage();
            images.put(desc, image);
        }
        return image;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        if (images != null) {
            for (Iterator i = images.values().iterator(); i.hasNext();) {
                ((Image) i.next()).dispose();
            }
            images = null;
        }
        super.dispose();
    }

    public Image getImage(Object element) {
        if (element instanceof OpAction) {
            ImageDescriptor desc = ((OpAction) element)
                    .getImageDescriptor();
            if (desc != null)
                return cacheImage(desc);
        } else if (element instanceof OperationCategory) {
            ImageDescriptor desc = WorkbenchImages
                    .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
            return cacheImage(desc);
        }
        return null;
    }

    public String getText(Object element) {
        String label = Messages.OperationLabelProvider_unknown;
        if (element instanceof OpAction) {
            label = ((OpAction) element).getText();
        } else if (element instanceof OperationCategory) {
            label = ((OperationCategory) element).getMenuText();
        }
        return DialogUtil.removeAccel(label);
    }
}
