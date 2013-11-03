/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.document.source;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.udig.catalog.document.IAbstractDocumentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.ContentType;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;
import org.locationtech.udig.document.model.AbstractDocument;
import org.locationtech.udig.document.model.ActionHotlinkDocument;
import org.locationtech.udig.document.model.DocumentFolder;
import org.locationtech.udig.document.model.FileAttachmentDocument;
import org.locationtech.udig.document.model.FileHotlinkDocument;
import org.locationtech.udig.document.model.FileLinkedDocument;
import org.locationtech.udig.document.model.WebHotlinkDocument;
import org.locationtech.udig.document.model.WebLinkedDocument;

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

