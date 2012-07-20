/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog;


/**
 * Document interface.
 * 
 * @author paul.pfeiffer
 * @author Naz Chan
 */
public interface IDocument extends IDocumentItem {

    /**
     * Document types
     */
    public enum Type {
        FILE, WEB;
    };
    
    /**
     * Gets the document label string.
     * 
     * @return label
     */
    public String getLabel();
    
    /**
     * Gets the attribute name of related to the document. This is only used by documents from
     * feature hotlinks.
     * 
     * @return attribute name
     */
    public String getAttributeName();
    
    /**
     * Gets the document type
     * 
     * @return document type
     */
    public Type getType();
    
    /**
     * Gets the document source of the document.
     * 
     * @return document source
     */
    public IAbstractDocumentSource getSource();
    
    /**
     * Gets the folder containing the document.
     * 
     * @return folder
     */
    public IDocumentFolder getFolder();

    /**
     * Open this document; in a platform specific manner.
     * <p>
     * As an example we will expect the operating system to open website URLs; or File references.
     * We may detect the presence of uDig specific things such as projects, SLDs or GetCapabilities
     * references and take appropriate action.
     */
    public boolean open();
    
    /**
     * Checks if the document has a related content (file or URL). Or if the value does not point to
     * a valid file or URL.
     * 
     * @return true if it has a related content, otherwise false
     */
    public boolean isEmpty();
    
}
