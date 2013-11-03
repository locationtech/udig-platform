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

import org.locationtech.udig.catalog.document.IAttachmentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocument.Type;
import org.locationtech.udig.catalog.document.IDocumentSource;
import org.locationtech.udig.catalog.document.IDocumentSource.DocumentInfo;
import org.locationtech.udig.catalog.internal.shp.ShpGeoResourceImpl;
import org.locationtech.udig.document.model.AbstractLinkedDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Shapefile feature-level attachment source implementation. This implements getters and setters to
 * the documents attached or linked to the shapefile's features. This document source copies
 * attachments to directory of the shapefile and also cleans up when an attachment is removed. See
 * {@link ShpDocPropertyParser} for details.
 * 
 * @author Naz Chan
 */
public class ShpAttachmentSource extends AbstractShpDocumentSource implements IAttachmentSource {
    
    public ShpAttachmentSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public boolean isEnabled() {
        return propParser.getFeatureDocsFlag();
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
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor) {
        docs = new ArrayList<IDocument>();
        final List<DocumentInfo> infos = propParser.getFeatureDocumentInfos(feature);
        if (infos != null && infos.size() > 0) {
            final List<IDocument> newDocs = docFactory.create(infos); 
            setFeature(newDocs, feature);
            docs.addAll(newDocs);
        }
        return docs;
    }
    
    private List<IDocument> getDocsInternal(SimpleFeature feature, IProgressMonitor monitor) {
        if (docs == null) {
            return getDocuments(feature, monitor);
        }
        return docs;
    }
    
    @Override
    public IDocument add(SimpleFeature feature, DocumentInfo info, IProgressMonitor monitor) {
        final IDocument doc = addInternal(feature, info, monitor);
        save(feature, monitor);
        return doc;
    }

    @Override
    public List<IDocument> add(SimpleFeature feature, List<DocumentInfo> infos, IProgressMonitor monitor) {
        final List<IDocument> newDocs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            addInternal(feature, info, monitor);
        }
        save(feature, monitor);
        return newDocs;
    }

    private IDocument addInternal(SimpleFeature feature, DocumentInfo info, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == info.getType()) {
            final File newFile = ShpDocUtils.copyFile(info.getInfo(), getAttachmentDir(feature));
            info.setInfo(newFile.getAbsolutePath());
        } else if (Type.LINKED == info.getType()) {
            // Do special handling here for linked documents, if needed
        }
        final IDocument newDoc = docFactory.create(info);
        setFeature(newDoc, feature);
        getDocsInternal(feature, monitor).add(newDoc);
        return newDoc;
    }
        
    @Override
    public boolean update(SimpleFeature feature, IDocument doc, DocumentInfo info, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == info.getType()) {
            final File oldFile = (File) doc.getContent();
            if (oldFile == null) {
                updateInternal(feature, oldFile, info);
            } else {
                if (!oldFile.getAbsolutePath().equals(info.getInfo())) {
                    updateInternal(feature, oldFile, info);
                }    
            }
        } else if (Type.LINKED == info.getType()) {
            // Do special handling here for linked documents, if needed
        }
        ((AbstractLinkedDocument) doc).setInfo(info);
        save(feature, monitor);
        return true;
    }

    private void updateInternal(SimpleFeature feature, File oldFile, DocumentInfo info) {
        ShpDocUtils.deleteFile(oldFile);
        final File newFile = ShpDocUtils.copyFile(info.getInfo(), getAttachmentDir(feature));
        info.setInfo(newFile.getAbsolutePath());
    }

    
    @Override
    public boolean remove(SimpleFeature feature, IDocument oldDoc, IProgressMonitor monitor) {
        removeInternal(feature, oldDoc, monitor);
        save(feature, monitor);
        return true;
    }

    @Override
    public boolean remove(SimpleFeature feature, List<IDocument> oldDocs, IProgressMonitor monitor) {
        for (IDocument oldDoc : oldDocs) {
            removeInternal(feature, oldDoc, monitor);
        }
        save(feature, monitor);
        return true;
    }

    private boolean removeInternal(SimpleFeature feature, IDocument oldDoc, IProgressMonitor monitor) {
        if (Type.ATTACHMENT == oldDoc.getType()) {
            ShpDocUtils.deleteFile(oldDoc.getContent());
        } else if (Type.LINKED == oldDoc.getType()) {
            // Do special handling here for linked documents, if needed
        }
        getDocsInternal(feature, monitor).remove(oldDoc);
        return true;
    }
    
    private void save(SimpleFeature feature, IProgressMonitor monitor) {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal(feature, monitor)) {
            if (Type.HOTLINK != doc.getType()) {
                final AbstractLinkedDocument shpDoc = (AbstractLinkedDocument) doc;
                infos.add(shpDoc.getInfo());    
            }
        }
        propParser.setFeatureDocumentInfos(feature, infos);
        propParser.writeProperties();
    }
    
    /**
     * Gets the attachment directory of the feature.
     * 
     * @param feature
     * @return attachment directory
     */
    private File getAttachmentDir(SimpleFeature feature) {
        return propParser.getFeatureAttachDir(feature.getIdentifier().getID());
    }
    
}
