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
package net.refractions.udig.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is the document folder implementation.
 * 
 * @author Naz Chan 
 */
public class DocumentFolder extends AbstractDocumentItem implements IDocumentFolder {

    private UUID id;
    private IAbstractDocumentSource source;
    
    private List<IDocumentItem> items;
    
    public DocumentFolder(String name, IAbstractDocumentSource source) {
        setName(name);
        this.source = source;
        this.items = new ArrayList<IDocumentItem>();
    }
    
    @Override
    public UUID getID() {
        return id;
    }
    
    public void setID(UUID id) {
        this.id = id;
    }
    
    @Override
    public IAbstractDocumentSource getSource() {
        return source;
    }

    @Override
    public List<IDocumentItem> getItems() {
        return items;
    }

    @Override
    public List<IDocument> getDocuments() {
        final List<IDocument> docs = new ArrayList<IDocument>();
        for (IDocumentItem item : items) {
            if (item instanceof IDocument) {
                docs.add((IDocument) item);
            }
        }
        return docs;
    }
    
    @Override
    public void setDocuments(List<IDocument> docs) {
        items.clear();
        addDocuments(docs);
    }
    
    @Override
    public boolean contains(IDocument doc) {
        return items.contains(doc);
    }
    
    @Override
    public void addItem(IDocumentItem item) {
        if (item != null) {
            items.add(item);    
        }
    }
    
    @Override
    public void addItems(List<IDocumentItem> items) {
        if (items != null && items.size() > 0) {
            for (IDocumentItem item : items) {
                items.add(item);
            }    
        }
    }
    
    @Override
    public void addDocuments(List<IDocument> docs) {
        if (docs != null && docs.size() > 0) {
            for (IDocument doc : docs) {
                items.add(doc);
            }    
        }
    }
    
    @Override
    public void addFolders(List<IDocumentFolder> folders) {
        if (folders != null && folders.size() > 0) {
            for (IDocumentFolder folder : folders) {
                items.add(folder);
            }            
        }
    }

    @Override
    public void removeItem(IDocumentItem item) {
        items.remove(item);
    }

    @Override
    public void removeDocuments(List<IDocument> docs) {
        items.removeAll(docs);
    }
    
}
