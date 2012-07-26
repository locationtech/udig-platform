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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opengis.filter.identity.FeatureId;

import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IAttachmentSource;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * This is the shapefile attachment source implementation. This implements getters and setters to
 * the documents attached or linked to the shapefile's features.
 * 
 * @author Naz Chan
 */
public class ShpAttachmentSource extends AbstractShpDocumentSource implements IAttachmentSource {

    public ShpAttachmentSource(ShpGeoResourceImpl geoResource) {
        super(geoResource);
    }

    @Override
    public List<IDocument> getDocuments(FeatureId fid) {
        return propParser.getFeatureAttachments(fid.getID());
    }

    @Override
    public IDocument addFile(FeatureId fid, File file) {
        final File localFile = copyFile(fid, file);
        if (localFile != null) {
            return add(fid, localFile);    
        }
        return null;
    }

    @Override
    public List<IDocument> addFiles(FeatureId fid, List<File> files) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (File file : files) {
            final IDocument doc = addFile(fid, file);
            if (doc != null) {
                docs.add(doc);
            }
        }
        return docs;
    }
    
    @Override
    public IDocument addLink(FeatureId fid, URL url) {
        return add(fid, url);
    }

    private File copyFile(FeatureId fid, File file) {
        try {
            final File attachmentsDir = propParser.getFeatureAttachmentsDir(fid.getID());
            if (!attachmentsDir.exists()) {
                attachmentsDir.mkdir();
            }
            FileUtils.copyFileToDirectory(file, attachmentsDir);
            return new File(attachmentsDir, file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private IDocument add(FeatureId fid, Object obj) {
        IDocument doc = null;
        if (obj instanceof File) {
            doc = docFactory.create((File) obj);
        } else if (obj instanceof URL) {
            doc = docFactory.create((URL) obj);
        }
        if (doc != null) {
            final List<IDocument> docs = getDocuments(fid);
            if (!docs.contains(doc)) {
                docs.add(doc);
                propParser.setFeatureAttachments(fid.getID(), docs);
                return doc;
            }
        }
        return null;
    }
    
    @Override
    public File updateFile(FeatureId fid, FileDocument doc, File file) {
        
        FileDocument fileDoc = null;
        final File localDir = propParser.getFeatureAttachmentsDir(fid.getID());
        File localFile = new File(localDir, file.getName());
        final IDocument newDoc = docFactory.create(localFile);
        final List<IDocument> docs = getDocuments(fid);
        for (IDocument existingDoc : docs) {
            if (existingDoc.equals(newDoc)) {
                return null;
            }
            if (existingDoc.equals(doc)) {
                fileDoc = (FileDocument) existingDoc;
            }
        }
        
        localFile = copyFile(fid, file);
        
        if (localFile != null && localFile.exists()) {
            fileDoc.getFile().delete();
            fileDoc.setFile(localFile);
            propParser.setFeatureAttachments(fid.getID(), docs);
            return localFile;
        }
        
        return null;
    }

    @Override
    public boolean updateLink(FeatureId fid, URLDocument doc, URL url) {

        URLDocument fileDoc = null;
        final IDocument newDoc = docFactory.create(url);
        final List<IDocument> docs = getDocuments(fid);
        for (IDocument existingDoc : docs) {
            if (existingDoc.equals(newDoc)) {
                return false;
            }
            if (existingDoc.equals(doc)) {
                fileDoc = (URLDocument) existingDoc;
            }
        }
        
        fileDoc.setUrl(url);
        propParser.setFeatureAttachments(fid.getID(), docs);
        return true;
        
    }

    @Override
    public boolean remove(FeatureId fid, IDocument doc) {
        return remove(fid, Collections.singletonList(doc));
    }

    @Override
    public boolean remove(FeatureId fid, List<IDocument> docs) {
        final List<IDocument> existingDocs = getDocuments(fid);
        for (IDocument doc : docs) {
            if (existingDocs.contains(doc)) {
                if (doc instanceof FileDocument) {
                    final FileDocument fileDoc = (FileDocument) doc;
                    fileDoc.getFile().delete();
                }
                existingDocs.remove(doc);
            }
        }
        propParser.setFeatureAttachments(fid.getID(), existingDocs);
        return true;
    }
    
}
