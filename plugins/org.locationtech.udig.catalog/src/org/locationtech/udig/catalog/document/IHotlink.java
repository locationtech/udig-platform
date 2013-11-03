/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.document;

import java.util.List;

import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

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
