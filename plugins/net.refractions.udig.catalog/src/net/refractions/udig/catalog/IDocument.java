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

import java.net.URI;

/**
 * Represents an available document.
 * <p>
 * IDocument items are currently made available to the DocumentView; but can be used elsewhere.
 * 
 * @author paul.pfeiffer
 */
public interface IDocument {
    
    /*
    enum Activity {
        OPEN,
        ATTACH,
        DELETE,
        EDIT,
        NEW
    }*/
    
    /**
     * The display name used to represent the document to the user
     * @return display name
     */
    public String getName();
    /**
     * Description of the document; for example this may be generated from the
     * modification date, creation date and so forth.
     * 
     * @return
     */
    public String getDescription();
    
    /**
     * Universal Resource Indicator.
     * 
     * In many cases this will be a simple File URL or web site reference. We are using a URI
     * here to allow for resolution using an internal OSGi bundle reference or database entry.
     * 
     * @return
     */
    public URI getURI();
    
    /**
     * This is the string reference available to be stored as a "hotlink" if required.
     * <p>
     * The default implementation is baed on getURI().toExternalForm().
     * 
     * @return String suitable for persistence
     */
    public String getReferences();
    
    /**
     * Open this document; in a platform specific manner.
     * <p>
     * As an example we will expect the operating system to open website URLs; or
     * File references. We may detect the presence of uDig specific things such as 
     * projects, SLDs or GetCapabilities references and take appropriate action.
     */
    public void open();

    /*
    Set<Activity> getActivity();
    
    public void perform( Activity activit, Object params... );
    */
}
