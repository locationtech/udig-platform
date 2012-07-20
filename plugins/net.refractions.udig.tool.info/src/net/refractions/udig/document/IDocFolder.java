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
package net.refractions.udig.document;

import java.util.List;
import java.util.UUID;

import net.refractions.udig.catalog.IAbstractDocumentSource;
import net.refractions.udig.catalog.IDocument;

/**
 * Document folder interface.
 * 
 * @author Naz Chan
 */
public interface IDocFolder {

    /**
     * Gets the ID of the document.
     * 
     * @return ID
     */
    public UUID getID();

    public String getName();
    
    /**
     * Gets the document source.
     * 
     * @return document source
     */
    public IAbstractDocumentSource getSource();

    /**
     * Checks if the document exists in the folder
     * 
     * @param doc
     * @return true if exists, otherwise false
     */
    public boolean contains(IDocument doc);

    /**
     * Checks if the folder exists in the folder
     * 
     * @param folder
     * @return true if exists, otherwise false
     */
    public boolean contains(IDocFolder folder);

    /**
     * Gets the contents of the folder
     * 
     * @return contents of the folder
     */
    public List<Object> getItems();

    /**
     * Gets the document items of the folder
     * 
     * @return documents
     */
    public List<IDocument> getDocuments();

    /**
     * Gets the folder items of the folder
     * 
     * @return folders
     */
    public List<IDocFolder> getFolders();

    /**
     * Sets the documents of the folder
     * 
     * @param docs
     */
    public void setDocuments(List<IDocument> docs);

    public void setFolders(List<IDocFolder> folders);

    /**
     * Adds the document item to the folder.
     * 
     * @param item
     */
    public void addDocument(IDocument doc);

    public void addFolder(IDocFolder folder);

    /**
     * Adds the documents to the folder.
     * 
     * @param docs
     */
    public void addDocuments(List<IDocument> docs);

    /**
     * Adds the document folders to the document.
     * 
     * @param folders
     */
    public void addFolders(List<IDocFolder> folders);

    /**
     * Removes the document item from the folder.
     * 
     * @param item
     */
    public void removeDocument(IDocument doc);
    
    public void removeDocuments(List<IDocument> docs);

    /**
     * Removed the documents from the folder.
     * 
     * @param docs
     */
    public void removeFolder(IDocFolder folder);

}
