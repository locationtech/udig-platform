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
 */
package net.refractions.udig.catalog.document;

import java.util.List;

import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * IDocument stored as a "hotlink" in the indicated {@link #getAttributeName()}.
 * 
 * @see IHotlinkSource
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public interface IHotlink extends IDocument {

    /**
     * Gets the attribute name of related to the document.
     * 
     * This is only used by documents from feature hotlinks.
     * 
     * @return attribute name
     */
    public String getAttributeName();
    
    /**
     * Gets the list of {@link HotlinkDescriptor} related to document.
     * 
     * @return list of descriptors
     */
    public List<HotlinkDescriptor> getDescriptors();
    
}
