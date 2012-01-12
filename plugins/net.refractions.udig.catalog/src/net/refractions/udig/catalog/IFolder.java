/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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

import java.util.List;

/**
 * Folder interface defining the functionality required to be able to view 
 * the data in a tree viewer. Used with the document view.
 * 
 * @author paul.pfeiffer
 * @version 1.3.1
 *
 */
public interface IFolder {

    /**
     * Returns the row at the given index
     * @param index
     * @return
     */
    public IFolder getRow(int index);
    
    /**
     * Sets the number of elements inside this folder 
     * @param count
     */
    public void setCount(int count);
    
    /**
     * Returns the number of elements within this folder
     * @return
     */
    public int getCount();
    
    /**
     * Returns the name of the folder
     * @return
     */
    public String getName();
}
