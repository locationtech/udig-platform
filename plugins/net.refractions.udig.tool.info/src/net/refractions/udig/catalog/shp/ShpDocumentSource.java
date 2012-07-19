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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.catalog.DocumentFolder;
import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IDocumentFolder;
import net.refractions.udig.catalog.IDocumentSource;
import net.refractions.udig.catalog.URLDocument;
import net.refractions.udig.catalog.internal.shp.ShpGeoResourceImpl;

/**
 * This is the shapefile document source implementation. This implements getters and setters to the
 * documents attached or linked to the shapefile.
 * 
 * @author Naz Chan
 */
public class ShpDocumentSource extends AbstractShpDocumentSource implements IDocumentSource {

    private static final String defaultLabel = "Shapefile Documents"; //$NON-NLS-1$
    private static final String namedLabelFormat = defaultLabel + " (%s)"; //$NON-NLS-1$
    
    /**
     * Creates a new ShpDocumentSource
     * 
     * @param url of the existing .shp file
     * @throws Exception
     */
    public ShpDocumentSource(ShpGeoResourceImpl resource) {
        super(resource, defaultLabel);
    }
    
    @Override
    public IDocumentFolder getDocumentsInFolder(String folderName) {
        if (folder instanceof DocumentFolder) {
            final DocumentFolder docfolder = (DocumentFolder) folder;
            docfolder.setName(String.format(namedLabelFormat, folderName));    
        }
        return getDocumentsInFolder();
    }
    
    @Override
    public IDocumentFolder getDocumentsInFolder() {
        folder.setDocuments(propParser.getShapeAttachments());
        return folder;
    }
    
    @Override
    public List<IDocument> getDocuments() {
        folder.setDocuments(propParser.getShapeAttachments());
        return folder.getDocuments();
    }
    
    @Override
    public boolean canAdd() {
        return true;
    }

    @Override 
    public IDocument addLink(URL url) {
        final IDocument doc = docFactory.create(url);
        if (!folder.contains(doc)) {
            folder.addItem(doc);
            propParser.setShapeAttachments(folder.getDocuments());
            return doc;
        }
        return null;
    }

    @Override
    public IDocument addFile(File file) {
        final IDocument doc = docFactory.create(file);
        if (!folder.contains(doc)) {
            folder.addItem(doc);
            propParser.setShapeAttachments(folder.getDocuments());
            return doc;
        }
        return null;
    }

    @Override
    public List<IDocument> addFiles(List<File> files) {
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (File file : files) {
            final IDocument doc = docFactory.create(file);
            if (!folder.contains(doc)) {
                docs.add(doc);
                folder.addItem(doc);
            }
        }
        propParser.setShapeAttachments(folder.getDocuments());
        return docs;
    }
    
    @Override
    public boolean canRemove() {
        return true;
    }

    @Override
    public void remove(IDocument doc) {
        remove(Collections.singletonList(doc));
    }

    @Override
    public void remove(List<IDocument> docs) {
        for (IDocument doc : docs) {
            if (folder.contains(doc)) {
                folder.removeItem(doc);
            }
        }
        propParser.setShapeAttachments(folder.getDocuments());
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean updateFile(FileDocument doc, File file) {
        if (folder.contains(docFactory.create(file))) {
            return false;
        }
        doc.setFile(file);
        propParser.setShapeAttachments(folder.getDocuments());
        return true;
    }

    @Override
    public boolean updateLink(URLDocument doc, URL url) {
        if (folder.contains(docFactory.create(url))) {
            return false;
        }
        doc.setUrl(url);
        propParser.setShapeAttachments(folder.getDocuments());
        return true;
    }

}
