/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
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
package net.refractions.udig.catalog.shp;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.catalog.internal.document.AbstractDocument;
import net.refractions.udig.catalog.internal.document.AttachmentFileDocument;
import net.refractions.udig.catalog.internal.document.DocumentFolder;
import net.refractions.udig.catalog.internal.document.FileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkActionDocument;
import net.refractions.udig.catalog.internal.document.HotlinkFileDocument;
import net.refractions.udig.catalog.internal.document.HotlinkWebDocument;
import net.refractions.udig.catalog.internal.document.WebDocument;

/**
 * Factory class to create shapefile documents and document folders.
 * 
 * @author Naz Chan
 */
public class ShpDocFactory {

    private IAbstractDocumentSource source;
    
    public ShpDocFactory(IAbstractDocumentSource source) {
        this.source = source;
    }

    /**
     * Creates a document from the document info.
     * 
     * @param info
     * @return document
     */
    public IDocument create(DocumentInfo info) {
        return create(info, false);
    }

    /**
     * Creates a document from the document info with the option to make the document an attachment.
     * 
     * @param info
     * @param isAttachment
     * @return document
     */
    public IDocument create(DocumentInfo info, boolean isAttachment) {
        AbstractDocument doc = null;
        switch (info.getType()) {
        case FILE:
            if (isAttachment) {
                doc = new AttachmentFileDocument(info);
            } else {
                doc = new FileDocument(info);
            }
            break;
        case WEB:
            doc = new WebDocument(info);
            break;
        default:
            break;
        }
        doc.setSource(source);
        return doc;
    }
    
    /**
     * Creates a list documents from the list of document infos.
     * 
     * @param infos
     * @return list of documents
     */
    public List<IDocument> create(List<DocumentInfo> infos) {
        return create(infos, false);
    }
    
    /**
     * Creates a list of document from the document infos with the option to make the document an
     * attachment.
     * 
     * @param infos
     * @param isAttachment
     * @return list of documents
     */
    public List<IDocument> create(List<DocumentInfo> infos, boolean isAttachment) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            docs.add(create(info, isAttachment));
        }
        return docs;
    }
    
    /**
     * Creates a document from the info and the list of hotlink descriptors.
     * 
     * @param info
     * @param descriptors
     * @return document
     */
    public IDocument create(String info, List<HotlinkDescriptor> descriptors) {
        
        AbstractDocument doc = null;
        switch (descriptors.get(0).getType()) {
        case FILE:
            doc = new HotlinkFileDocument(info, descriptors);
            break;
        case WEB:
            doc = new HotlinkWebDocument(info, descriptors);
            break;
        case ACTION:
            doc = new HotlinkActionDocument(info, descriptors);
            break;
        default:
            break;
        }
        doc.setSource(source);

        return doc;

    }
    
    /**
     * Creates a document folder.
     * 
     * @param name
     * @param source
     * @return document folder
     */
    public static DocumentFolder createFolder(String name, IAbstractDocumentSource source) {
        return new DocumentFolder(name, source);
    }
    
}

