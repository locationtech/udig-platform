/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.source;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;
import org.locationtech.udig.document.model.AbstractLinkedDocument;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Shapefile resource-level document source implementation. This implements getters and setters to
 * the documents attached to the resource.
 * 
 * @author Naz Chan
 */
public class ShpDocumentSource extends AbstractShpDocumentSource implements IDocumentSource {

    /**
     * Creates a new ShpDocumentSource
     * 
     * @param url of the existing .shp file
     * @throws Exception
     */
    public ShpDocumentSource(ShpGeoResourceImpl resource) {
        super(resource);
    }

    @Override
    public boolean isEnabled() {
        return propParser.getShapefileFlag();
    }
    
    @Override
    public boolean isEnabledEditable() {
        return true;
    }
    
    @Override
    public boolean canAttach() {
        return true;
    }

    @Override
    public boolean canLinkFile() {
        // Not implemented in this document source
        return false;
    }
    
    @Override
    public boolean canLinkWeb() {
        return true;
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean canRemove() {
        return true;
    }
    
    @Override
    public List<IDocument> getDocuments(IProgressMonitor monitor) {
        docs = new ArrayList<IDocument>();
        final List<DocumentInfo> infos = propParser.getShapeDocumentInfos();
        if (infos != null && infos.size() > 0) {
            docs.addAll(docFactory.create(infos));
        }
        return docs;
    }

    private List<IDocument> getDocsInternal(IProgressMonitor monitor) {
        if (docs == null) {
            return getDocuments(monitor);
        }
        return docs;
    }
        
    @Override
    public IDocument add(DocumentInfo info, IProgressMonitor monitor) {
        final IDocument doc = addInternal(info, monitor);
        save(monitor);
        return doc;
    }

    @Override
    public List<IDocument> add(List<DocumentInfo> infos, IProgressMonitor monitor) {
        final List<IDocument> newDocs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            addInternal(info, monitor);
        }
        save(monitor);
        return newDocs;
    }

    private IDocument addInternal(DocumentInfo info, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == info.getType()) {
            final File newFile = ShpDocUtils.copyFile(info.getInfo(), propParser.getShapefileAttachDir());
            info.setInfo(newFile.getAbsolutePath());
        } else if (Type.LINKED == info.getType()) {
            // Do special handling here for linked documents, if needed
        }
        final IDocument newDoc = docFactory.create(info);
        getDocsInternal(monitor).add(newDoc);
        return newDoc;
    }

    @Override
    public boolean remove(IDocument oldDoc, IProgressMonitor monitor) {
        removeInternal(oldDoc, monitor);
        save(monitor);
        return true;
    }

    @Override
    public boolean remove(List<IDocument> oldDocs, IProgressMonitor monitor) {
        for (IDocument doc : oldDocs) {
            removeInternal(doc, monitor);
        }
        save(monitor);
        return true;
    }

    private boolean removeInternal(IDocument doc, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == doc.getType()) {
            ShpDocUtils.deleteFile(doc.getContent());
        } else if (Type.LINKED == doc.getType()) {
            // Do special handling here for linked documents, if needed
        }
        getDocsInternal(monitor).remove(doc);
        return true;
    }
    
    @Override
    public boolean update(IDocument doc, DocumentInfo info, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == info.getType()) {
            final File oldFile = (File) doc.getContent();
            if (oldFile == null) {
                updateInternal(oldFile, info);
            } else {
                if (!oldFile.getAbsolutePath().equals(info.getInfo())) {
                    updateInternal(oldFile, info);
                }    
            }
        } else if (Type.LINKED == info.getType()) {
            // Do special handling here for linked documents, if needed
        }
        ((AbstractLinkedDocument) doc).setInfo(info);
        save(monitor);
        return true;
    }
 
    private void updateInternal(File oldFile, DocumentInfo info) {
        ShpDocUtils.deleteFile(oldFile);
        final File newFile = ShpDocUtils.copyFile(info.getInfo(), propParser.getShapefileAttachDir());
        info.setInfo(newFile.getAbsolutePath());
    }
    
    private void save(IProgressMonitor monitor) {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal(monitor)) {
            final AbstractLinkedDocument shpDoc = (AbstractLinkedDocument) doc;
            infos.add(shpDoc.getInfo());
        }
        propParser.setShapeDocmentInfos(infos);
        propParser.writeProperties();
    }
    
}
