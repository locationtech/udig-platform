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

import java.util.List;
import java.util.UUID;

/**
 * Document folder interface.
 * 
 * @author Naz Chan 
 */
public interface IDocumentFolder extends IDocumentItem {

    /**
     * Gets the ID of the document.
     * 
     * @return ID
     */
    public UUID getID();
    
    /**
     * Gets the document source.
     * 
     * @return document source
     */
    public IAbstractDocumentSource getSource();
    
    /**
     * Gets the contents of the folder
     * 
     * @return contents of the folder
     */
    public List<IDocumentItem> getItems();
    
    /**
     * Gets the document contents of the folder
     * @return document contents
     */
    public List<IDocument> getDocuments();
    
    /**
     * Sets the documents of the folder
     * 
     * @param docs
     */
    public void setDocuments(List<IDocument> docs);
    
    /**
     * Checks if the document exists in the folder
     * 
     * @param doc
     * @return true if exists, otherwise false
     */
    public boolean contains(IDocument doc);
    
    /**
     * Adds the document item to the folder.
     * 
     * @param item
     */
    public void addItem(IDocumentItem item);
    
    /**
     * Adds the document items to the folder.
     * 
     * @param items
     */
    public void addItems(List<IDocumentItem> items);
    
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
    public void removeItem(IDocumentItem item);
    
    /**
     * Removed the documents from the folder.
     * 
     * @param docs
     */
    public void removeDocuments(List<IDocument> docs);
    
}
