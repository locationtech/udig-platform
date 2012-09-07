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
package net.refractions.udig.document.source;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IAttachmentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;
import net.refractions.udig.document.model.AbstractLinkedDocument;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

/**
 * This is the shapefile attachment source implementation. This implements getters and setters to
 * the documents attached or linked to the shapefile's features. This document source copies
 * attachments to directory of the shapefile and also cleans up when an attachment is removed. See
 * {@link ShpDocPropertyParser} for details.
 * 
 * @author Naz Chan
 */
public class ShpAttachmentSource extends ShpHotlinkSource implements IAttachmentSource {
    
    public ShpAttachmentSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public List<IDocument> getDocuments(SimpleFeature feature, IProgressMonitor monitor) {
        super.getDocuments(feature);
        final List<DocumentInfo> infos = propParser.getFeatureDocumentInfos(feature);
        if (infos != null && infos.size() > 0) {
            docs.addAll(docFactory.create(infos));
        }
        return docs;
    }
    
    private List<IDocument> getDocsInternal(SimpleFeature feature) {
        if (docs == null) {
            return getDocuments(feature);
        }
        return docs;
    }
    
    @Override
    public boolean canAttach() {
        return true;
    }
    
    @Override
    public boolean canLinkFile() {
        return false;
    }
    
    @Override
    public boolean canLinkWeb() {
        return true;
    }
    
    @Override
    public IDocument add(SimpleFeature feature, DocumentInfo info, IProgressMonitor monitor) {
        final IDocument doc = addInternal(feature, info);
        save(feature);
        return doc;
    }

    @Override
    public List<IDocument> add(SimpleFeature feature, List<DocumentInfo> infos, IProgressMonitor monitor) {
        final List<IDocument> newDocs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            addInternal(feature, info);
        }
        save(feature);
        return newDocs;
    }

    private IDocument addInternal(SimpleFeature feature, DocumentInfo info) {
        if (Type.ATTACHMENT == info.getType()) {
            final File newFile = ShpDocUtils.copyFile(info.getInfo(), getAttachmentDir(feature));
            info.setInfo(newFile.getAbsolutePath());
        } else if (Type.LINKED == info.getType()) {
            // Do special handling here for linked documents, if needed
        }
        final IDocument newDoc = docFactory.create(info);
        getDocsInternal(feature).add(newDoc);
        return newDoc;
    }
    
    @Override
    public boolean canUpdate() {
        return true;
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
        save(feature);
        return true;
    }

    private void updateInternal(SimpleFeature feature, File oldFile, DocumentInfo info) {
        ShpDocUtils.deleteFile(oldFile);
        final File newFile = ShpDocUtils.copyFile(info.getInfo(), getAttachmentDir(feature));
        info.setInfo(newFile.getAbsolutePath());
    }
    
    @Override
    public boolean canRemove() {
        return true;
    }
    
    @Override
    public boolean remove(SimpleFeature feature, IDocument oldDoc, IProgressMonitor monitor) {
        removeInternal(feature, oldDoc);
        save(feature);
        return true;
    }

    @Override
    public boolean remove(SimpleFeature feature, List<IDocument> oldDocs, IProgressMonitor monitor) {
        for (IDocument oldDoc : oldDocs) {
            removeInternal(feature, oldDoc);
        }
        save(feature);
        return true;
    }

    private boolean removeInternal(SimpleFeature feature, IDocument oldDoc) {
        if (Type.ATTACHMENT == oldDoc.getType()) {
            ShpDocUtils.deleteFile(oldDoc.getContent());
        } else if (Type.LINKED == oldDoc.getType()) {
            // Do special handling here for linked documents, if needed
        }
        getDocsInternal(feature).remove(oldDoc);
        return true;
    }
    
    private void save(SimpleFeature feature) {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal(feature)) {
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
