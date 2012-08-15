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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IAttachmentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocument.DocType;
import net.refractions.udig.catalog.document.IDocument.Type;
import net.refractions.udig.catalog.document.IDocumentSource;
import net.refractions.udig.catalog.document.IDocumentSource.DocumentInfo;
import net.refractions.udig.catalog.internal.document.AbstractBasicDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

import org.apache.commons.io.FileUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;

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
    public List<IDocument> getDocuments(SimpleFeature feature) {
        super.getDocuments(feature);
        final List<DocumentInfo> infos = propParser.getFeatureDocumentInfos(feature);
        if (infos != null && infos.size() > 0) {
            docs.addAll(docFactory.create(infos, true));
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
    public IDocument add(SimpleFeature feature, DocumentInfo info) {
        if (Type.FILE == info.getType()) {
            final File newFile = copyFile(feature.getIdentifier(), info.getInfo());
            info.setInfo(newFile.getAbsolutePath());
        }
        final IDocument newDoc = docFactory.create(info, true);
        getDocsInternal(feature).add(newDoc);
        save(feature);
        return newDoc;
    }

    @Override
    public List<IDocument> add(SimpleFeature feature, List<DocumentInfo> infos) {
        final List<IDocument> newDocs = new ArrayList<IDocument>();
        for (DocumentInfo info : infos) {
            newDocs.add(add(feature, info));
        }
        save(feature);
        return newDocs;
    }

    @Override
    public IDocument update(SimpleFeature feature, IDocument doc, DocumentInfo info) {
        if (Type.FILE == info.getType()) {
            final File oldFile = (File) doc.getValue();
            if (oldFile == null) {
                updateAttachment(feature, oldFile, info);
            } else {
                if (!oldFile.getAbsolutePath().equals(info.getInfo())) {
                    updateAttachment(feature, oldFile, info);
                }    
            }
        }
        ((AbstractBasicDocument) doc).setInfo(info);
        save(feature);
        return doc;
    }

    private void updateAttachment(SimpleFeature feature, File oldFile, DocumentInfo info) {
        deleteFile(oldFile);
        final File newFile = copyFile(feature.getIdentifier(), info.getInfo());
        info.setInfo(newFile.getAbsolutePath());
    }
    
    @Override
    public boolean remove(SimpleFeature feature, IDocument oldDoc) {
        removeInternal(feature, oldDoc);
        save(feature);
        return true;
    }

    @Override
    public boolean remove(SimpleFeature feature, List<IDocument> oldDocs) {
        for (IDocument oldDoc : oldDocs) {
            removeInternal(feature, oldDoc);
        }
        save(feature);
        return true;
    }

    private boolean removeInternal(SimpleFeature feature, IDocument oldDoc) {
        if (Type.FILE == oldDoc.getType()) {
            deleteFile(oldDoc.getValue());
        }
        getDocsInternal(feature).remove(oldDoc);
        return true;
    }
    
    /**
     * Deletes the file attachment.
     * 
     * @param fileObj
     * @return true if successful, otherwise false
     */
    private boolean deleteFile(Object fileObj) {
        if (fileObj != null) {
            final File oldFile = (File) fileObj;
            if (oldFile.exists()) {
                return oldFile.delete();    
            }
        }
        return false;
    }
    
    /**
     * Copies the file to the feature's documents directory.
     * 
     * @param fid
     * @param file
     * @return new file
     */
    private File copyFile(FeatureId fid, String filePath) {
        try {
            final File attachDir = propParser.getFeatureDocumentsDir(fid.getID());
            if (!attachDir.exists()) {
                attachDir.mkdir();
            }
            final File oldFile = new File(filePath);
            final File newFile = new File(attachDir, oldFile.getName());
            if (!newFile.exists()) {
                FileUtils.copyFileToDirectory(oldFile, attachDir);    
            }
            return newFile;
        } catch (IOException e) {
            // Should not happen
            e.printStackTrace();
        }
        return null;
    }
    
    private void save(SimpleFeature feature) {
        final List<DocumentInfo> infos = new ArrayList<IDocumentSource.DocumentInfo>();
        for (IDocument doc : getDocsInternal(feature)) {
            if (DocType.HOTLINK != doc.getDocType()) {
                final AbstractBasicDocument shpDoc = (AbstractBasicDocument) doc;
                infos.add(shpDoc.getInfo());    
            }
        }
        propParser.setFeatureDocumentInfos(feature, infos);
        propParser.writeProperties();
    }
    
}
