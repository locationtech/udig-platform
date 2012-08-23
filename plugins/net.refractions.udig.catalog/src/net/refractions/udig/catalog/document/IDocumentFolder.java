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
package net.refractions.udig.catalog.document;

import java.util.List;


/**
 * Document folder interface.
 * 
 * @author Naz Chan 
 */
public interface IDocumentFolder {
    
    /**
     * Gets the display name of the folder;
     * 
     * @return name
     */
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
    public boolean contains(IDocumentFolder folder);

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
    public List<IDocumentFolder> getFolders();

    /**
     * Sets the documents of the folder
     * 
     * @param docs
     */
    public void setDocuments(List<IDocument> docs);

    /**
     * Sets the folders of the folder.
     * 
     * @param folders
     */
    public void setFolders(List<IDocumentFolder> folders);

    /**
     * Adds the document item to the folder.
     * 
     * @param item
     */
    public void addDocument(IDocument doc);

    /**
     * Adds folder.
     * 
     * @param folder
     */
    public void addFolder(IDocumentFolder folder);

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
    public void addFolders(List<IDocumentFolder> folders);

    /**
     * Removes the document item from the folder.
     * 
     * @param item
     */
    public void removeDocument(IDocument doc);
    
    /**
     * Removes the documents from teh folder.
     * 
     * @param docs
     */
    public void removeDocuments(List<IDocument> docs);

    /**
     * Removed the documents from the folder.
     * 
     * @param docs
     */
    public void removeFolder(IDocumentFolder folder);
    
}
