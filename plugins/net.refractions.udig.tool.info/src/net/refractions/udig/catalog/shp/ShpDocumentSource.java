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
package net.refractions.udig.catalog.shp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.ContentType;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.internal.document.AbstractBasicDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * This is the shapefile document source implementation. This implements getters and setters to the
 * documents attached to the shapefile.
 * 
 * @author Naz Chan
 */
public class ShpDocumentSource extends AbstractShpDocumentSource implements IDocumentSource {

    private List<IDocument> docs;
    
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
    public List<IDocument> getDocuments() {
        docs = new ArrayList<IDocument>();
        final List<DocumentInfo> infos = propParser.getShapeDocumentInfos();
        if (infos != null && infos.size() > 0) {
            docs.addAll(docFactory.create(infos, true));
        }
        return docs;
    }

    private List<IDocument> getDocsInternal() {
        if (docs == null) {
            return getDocuments();
        }
        return docs;
    }
        
    @Override
    public boolean canAttach() {
        return true;
    }

    @Override
    public boolean canLinkFile() {
        return false; // Not implemented in this document source
    }
    
    @Override
    public boolean canLinkWeb() {
        return true;
    }
    
    @Override
    public IDocument add(DocumentInfo info) {
        final IDocument doc = addInternal(info);
        save();
        return doc;
    }

    @Override
    public List<IDocument> add(List<DocumentInfo> infos) {
        final List<IDocument> newDocs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            addInternal(info);
        }
        save();
        return newDocs;
    }

    private IDocument addInternal(DocumentInfo info) {
        if (ContentType.FILE == info.getType()) {
            final File newFile = ShpDocUtils.copyFile(info.getInfo(), propParser.getShapefileAttachDir());
            info.setInfo(newFile.getAbsolutePath());
        }
        final IDocument newDoc = docFactory.create(info, true);
        getDocsInternal().add(newDoc);
        return newDoc;
    }
    
    @Override
    public boolean canRemove() {
        return true;
    }

    @Override
    public boolean remove(IDocument oldDoc) {
        removeInternal(oldDoc);
        save();
        return true;
    }

    @Override
    public boolean remove(List<IDocument> oldDocs) {
        for (IDocument doc : oldDocs) {
            removeInternal(doc);
        }
        save();
        return true;
    }

    private boolean removeInternal(IDocument doc) {
        if (ContentType.FILE == doc.getContentType()) {
            ShpDocUtils.deleteFile(doc.getContent());
        }
        getDocsInternal().remove(doc);
        return true;
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public IDocument update(IDocument doc, DocumentInfo info) {
        if (ContentType.FILE == info.getType()) {
            final File oldFile = (File) doc.getContent();
            if (oldFile == null) {
                updateInternal(oldFile, info);
            } else {
                if (!oldFile.getAbsolutePath().equals(info.getInfo())) {
                    updateInternal(oldFile, info);
                }    
            }
        }
        ((AbstractBasicDocument) doc).setInfo(info);
        save();
        return doc;
    }
 
    private void updateInternal(File oldFile, DocumentInfo info) {
        ShpDocUtils.deleteFile(oldFile);
        final File newFile = ShpDocUtils.copyFile(info.getInfo(), propParser.getShapefileAttachDir());
        info.setInfo(newFile.getAbsolutePath());
    }
    
    private void save() {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal()) {
            final AbstractBasicDocument shpDoc = (AbstractBasicDocument) doc;
            infos.add(shpDoc.getInfo());
        }
        propParser.setShapeDocmentInfos(infos);
        propParser.writeProperties();
    }
    
}
