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
package net.refractions.udig.catalog.internal.document;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.document.IAbstractDocumentSource;
import net.refractions.udig.catalog.document.IDocument;
import net.refractions.udig.catalog.document.IDocumentFolder;

/**
 * This is the document folder implementation.
 * 
 * @author Naz Chan
 */
public class DocumentFolder implements IDocumentFolder {

    private String name;

    private IAbstractDocumentSource source;

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
    public IAbstractDocumentSource getSource() {
        return source;
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
