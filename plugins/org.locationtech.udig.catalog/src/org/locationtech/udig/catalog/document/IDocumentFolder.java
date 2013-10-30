/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

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
     * Adds the documents to the folder at the index.
     * 
     * @param docs
     * @param index
     */
    public void insertDocuments(List<IDocument> docs, int index);
    
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
