/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.ui;

import java.io.File;

import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.document.DocumentPlugin;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Image provider for document items. This integrates getting the right image for the document item
 * type, document type and/or file type with creating overlay decorators.
 * 
 * @author Naz Chan
 */
public class DocumentImageProvider extends CompositeImageDescriptor {

    private ImageData activeImgData = null;
    
    private ImageData topOverlayImgData = null;
    private ImageData bottomOverlayImgData = null;
    
    private Point size;
    
    /**
     * Default width for the icon.
     */
    private static final int DEFAULT_WIDTH = 20;
    /**
     * Default height for the icon.
     */
    private static final int DEFAULT_HEIGHT = 16;
    
    public DocumentImageProvider() {
        size = new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    protected void drawCompositeImage(int width, int height) {
        drawImage(activeImgData, 0, 0);
        if (topOverlayImgData != null) {
            final int xPos = getSize().x - topOverlayImgData.width;
            final int yPos = 0;
            drawImage(topOverlayImgData, xPos, yPos);
        }
        if (bottomOverlayImgData != null) {
            final int xPos = getSize().x - bottomOverlayImgData.width + 1;
            final int yPos = getSize().y - bottomOverlayImgData.height + 1;
            drawImage(bottomOverlayImgData, xPos, yPos);
        }        
    }
    
    @Override
    protected Point getSize() {
        return size;
    }
    
    private ImageData getFileImageData(File file) {
        if (file != null) {
            final String extension = DocUtils.getExtension(file);
            if (extension != null) {
                final Program program = Program.findProgram(extension);
                if (program != null) {
                    return program.getImageData();    
                }                
            }
        }
        return null;
    }
    
    private ImageData getDefaultImageData(ContentType contentType) {
        
        ImageDescriptor descriptor = null;
        switch (contentType) {
        case FILE:
            descriptor = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
            break;
        case WEB:
            descriptor = DocumentPlugin.getDefault().getImageRegistry()
                    .getDescriptor(IDocumentImages.IMG_OBJ_LINK);
            break;
        case ACTION:
            descriptor = DocumentPlugin.getDefault().getImageRegistry()
                    .getDescriptor(IDocumentImages.IMG_OBJ_ACTION);
            break;
        default:
            descriptor = PlatformUI.getWorkbench().getSharedImages()
                    .getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
            break;
        }
        
        if (descriptor != null) {
            return descriptor.getImageData();
        }
        return null;
        
    }
    
    private ImageData getAttachmentOverlay() {
        return DocumentPlugin.getDefault().getImageRegistry()
                .getDescriptor(IDocumentImages.IMG_OVR_ATTACHMENT).getImageData();
    }
    
    private ImageData getHotlinkOverlay() {
        return DocumentPlugin.getDefault().getImageRegistry()
                .getDescriptor(IDocumentImages.IMG_OVR_HOTLINK).getImageData();
    }
    
    private ImageData getTemplateOverlay() {
        return DocumentPlugin.getDefault().getImageRegistry()
                .getDescriptor(IDocumentImages.IMG_OVR_TEMPLATE).getImageData();
    }
    
    private Image createDocumentImage(Type type, ContentType contentType, File file,
            boolean isTemplate) {

        activeImgData = null;
        if (ContentType.FILE == contentType) {
            activeImgData = getFileImageData(file);
        }
        if (activeImgData == null) {
            activeImgData = getDefaultImageData(contentType);
        }
        
        topOverlayImgData = null;
        if (Type.ATTACHMENT == type) {
            topOverlayImgData = getAttachmentOverlay();
        } if (Type.HOTLINK == type) {
            topOverlayImgData = getHotlinkOverlay();
        }
        
        bottomOverlayImgData = null;
        if (isTemplate) {
            bottomOverlayImgData = getTemplateOverlay();
        }
        
        return createImage();
    }
    
    /**
     * Creates an icon for a document. This method draws needed decorators as per document type.
     * 
     * @param doc
     * @return icon
     */
    public Image createDocumentImage(IDocument doc) {
        final Type type = doc.getType();
        final ContentType contentType = doc.getContentType();
        File file = null;
        if (ContentType.FILE == contentType) {
            file = (File) doc.getContent();
        }
        return createDocumentImage(type, contentType, file, doc.isTemplate());
    }

    /**
     * Creates an icon for a folder.
     * 
     * @return icon
     */
    public Image createFolderImage() {
        activeImgData = PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER).getImageData();
        topOverlayImgData = null;
        bottomOverlayImgData = null;
        return createImage();
    }

    /**
     * Creates the default icon for document items.
     * 
     * @return icon
     */
    public Image createDefaultImage() {
        activeImgData = PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT).getImageData();
        topOverlayImgData = null;
        bottomOverlayImgData = null;
        return createImage();
    }
    
}
