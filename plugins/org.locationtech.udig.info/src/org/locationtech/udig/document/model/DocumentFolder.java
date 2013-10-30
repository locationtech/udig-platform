/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.model;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.document.IAbstractDocumentSource;
import org.locationtech.udig.catalog.document.IDocument;
import org.locationtech.udig.catalog.document.IDocumentFolder;

/**
 * This is the document folder implementation.
 * 
 * @author Naz Chan
 */
public class DocumentFolder extends AbstractDocumentItem implements IDocumentFolder {

    private String name;
    
    private List<IDocumentFolder> folders;
    private List<IDocument> docs;

    public DocumentFolder(String name, IAbstractDocumentSource source) {
        this.name = name;
        this.source = source;
        this.folders = new ArrayList<IDocumentFolder>();
        this.docs = new ArrayList<IDocument>();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean contains(IDocument doc) {
        return docs.contains(doc);
    }

    @Override
    public boolean contains(IDocumentFolder folder) {
        return folders.contains(folder);
    }

    @Override
    public List<Object> getItems() {
        final List<Object> items = new ArrayList<Object>();
        items.addAll(folders);
        items.addAll(docs);
        return items;
    }

    @Override
    public List<IDocument> getDocuments() {
        return docs;
    }

    @Override
    public List<IDocumentFolder> getFolders() {
        return folders;
    }

    @Override
    public void setDocuments(List<IDocument> docs) {
        this.docs = docs;
    }

    @Override
    public void setFolders(List<IDocumentFolder> folders) {
        this.folders = folders;
    }

    @Override
    public void addDocument(IDocument doc) {
        this.docs.add(doc);
    }

    @Override
    public void addFolder(IDocumentFolder folder) {
        this.folders.add(folder);
    }

    @Override
    public void addDocuments(List<IDocument> docs) {
        this.docs.addAll(docs);
    }

    @Override
    public void addFolders(List<IDocumentFolder> folders) {
        this.folders.addAll(folders);
    }

    @Override
    public void insertDocuments(List<IDocument> docs, int index) {
        this.docs.addAll(index, docs);
    }

    @Override
    public void removeDocument(IDocument doc) {
        this.docs.remove(doc);
    }

    @Override
    public void removeDocuments(List<IDocument> docs) {
        this.docs.removeAll(docs);
    }

    @Override
    public void removeFolder(IDocumentFolder folder) {
        this.folders.remove(folder);
    }

}
