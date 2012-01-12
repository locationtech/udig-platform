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
 * Abstract class that implements hashCode and equals 
 * so that instances of this object can be added and removed in lists. 
 * Also contains methods for IFolder to enable it to be added to a folder and 
 * defined as a leaf node in the tree.
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public abstract class AbstractDocument implements IDocument, IFolder {

    /* (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getName()
     */
    @Override
    public abstract String getName();

    /* (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getDescription()
     */
    @Override
    public abstract String getDescription();

    /* (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getURI()
     */
    @Override
    public abstract URI getURI();

    /* (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#getReferences()
     */
    @Override
    public abstract String getReferences();

    /* (non-Javadoc)
     * @see net.refractions.udig.catalog.IDocument#open()
     */
    @Override
    public abstract void open();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getReferences() == null) ? 0 : getReferences().hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof AbstractDocument) {
            AbstractDocument document = (AbstractDocument) obj;
            if (getReferences().equals(document.getReferences())) {
                return true;
            }
        }
        return false;
    }

    // defines this document as a leaf node in a tree view. See IFolder
    @Override
    public IFolder getRow(int index) {
        return null;
    }
    // defines this document as a leaf node in a tree view. See IFolder
    @Override
    public void setCount(int count) {
        
    }
    // defines this document as a leaf node in a tree view. See IFolder
    @Override
    public int getCount() {
        return 0;
    }
    
    @Override
    public String toString() {
        return getReferences();
    }
    
}
