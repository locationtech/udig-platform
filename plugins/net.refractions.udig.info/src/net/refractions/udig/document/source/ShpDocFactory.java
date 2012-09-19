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
package net.refractions.udig.document.source;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import net.refractions.udig.document.model.AbstractDocument;
import net.refractions.udig.document.model.ActionHotlinkDocument;
import net.refractions.udig.document.model.DocumentFolder;
import net.refractions.udig.document.model.FileAttachmentDocument;
import net.refractions.udig.document.model.FileHotlinkDocument;
import net.refractions.udig.document.model.FileLinkedDocument;
import net.refractions.udig.document.model.WebHotlinkDocument;
import net.refractions.udig.document.model.WebLinkedDocument;

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
        AbstractDocument doc = null;
        if (Type.ATTACHMENT == info.getType()) {
            if (ContentType.FILE == info.getContentType()) {
                doc = new FileAttachmentDocument(info);
            }
        } else if (Type.LINKED == info.getType()) {
            if (ContentType.FILE == info.getContentType()) {
                doc = new FileLinkedDocument(info);
            } else if (ContentType.WEB == info.getContentType()) {
                doc = new WebLinkedDocument(info);
            }
        }
        if (doc != null) {
            doc.setSource(source);    
        }
        return doc;
    }
    
    /**
     * Creates a list of document from the document infos.
     * 
     * @param infos
     * @return list of documents
     */
    public List<IDocument> create(List<DocumentInfo> infos) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            docs.add(create(info));
        }
        return docs;
    }
    
    /**
     * Creates a document from the info (is the attribute value) and the list of hotlink descriptors
     * related to an attribute.
     * 
     * @param info
     * @param descriptors
     * @return document
     */
    public IDocument create(String info, List<HotlinkDescriptor> descriptors) {
        AbstractDocument doc = null;
        switch (descriptors.get(0).getType()) {
        case FILE:
            doc = new FileHotlinkDocument(info, descriptors);
            break;
        case WEB:
            doc = new WebHotlinkDocument(info, descriptors);
            break;
        case ACTION:
            doc = new ActionHotlinkDocument(info, descriptors);
            break;
        default:
            break;
        }
        if (doc != null) {
            doc.setSource(source);    
        }
        return doc;
    }
    
    /**
     * Creates a document folder.
     * 
     * @param name
     * @param source
     * @return document folder
     */
    public static DocumentFolder createFolder(SimpleFeature feature, String name,
            IAbstractDocumentSource source) {
        final DocumentFolder folder = new DocumentFolder(name, source);
        folder.setFeature(feature);
        return folder;
    }
    
}

