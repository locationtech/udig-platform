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

import net.refractions.udig.catalog.FileDocument;
import net.refractions.udig.catalog.IDocument;
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
        return propParser.getShapeAttachments();
    }
    
    @Override
    public boolean canAdd() {
        return true;
    }

    @Override 
    public IDocument addLink(URL url) {
        final List<IDocument> docs = getDocuments();
        final IDocument doc = docFactory.create(url);
        if (!docs.contains(doc)) {
            docs.add(doc);
            propParser.setShapeAttachments(docs);
            return doc;
        }
        return null;
    }

    @Override
    public IDocument addFile(File file) {
        final List<IDocument> docs = getDocuments();
        final IDocument doc = docFactory.create(file);
        if (!docs.contains(doc)) {
            docs.add(doc);
            propParser.setShapeAttachments(docs);
            return doc;
        }
        return null;
    }

    @Override
    public List<IDocument> addFiles(List<File> files) {
        final List<IDocument> existingDocs = getDocuments();
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (File file : files) {
            final IDocument doc = docFactory.create(file);
            if (!existingDocs.contains(doc)) {
                docs.add(doc);
                existingDocs.add(doc);
            }
        }
        propParser.setShapeAttachments(existingDocs);
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
        final List<IDocument> existingDocs = getDocuments();
        for (IDocument doc : docs) {
            if (existingDocs.contains(doc)) {
                existingDocs.remove(doc);
            }
        }
        propParser.setShapeAttachments(existingDocs);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public boolean updateFile(FileDocument doc, File file) {
        
        FileDocument fileDoc = null;
        final IDocument newDoc = docFactory.create(file);
        final List<IDocument> docs = getDocuments();
        for (IDocument existingDoc : docs) {
            if (existingDoc.equals(newDoc)) {
                return false;
            }
            if (existingDoc.equals(doc)) {
                fileDoc = (FileDocument) existingDoc;
            }
        }
        
        fileDoc.setFile(file);
        propParser.setShapeAttachments(docs);
        return true;
        
    }

    @Override
    public boolean updateLink(URLDocument doc, URL url) {
        
        URLDocument fileDoc = null;
        final IDocument newDoc = docFactory.create(url);
        final List<IDocument> docs = getDocuments();
        for (IDocument existingDoc : docs) {
            if (existingDoc.equals(newDoc)) {
                return false;
            }
            if (existingDoc.equals(doc)) {
                fileDoc = (URLDocument) existingDoc;
            }
        }
        
        fileDoc.setUrl(url);
        propParser.setShapeAttachments(docs);
        return true;

    }

}
