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
package net.refractions.udig.catalog.internal.document;

import java.util.List;

import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * Document model for hotlink action documents.
 * 
 * @author Naz Chan
 */
public class HotlinkActionDocument extends AbstractHotlinkDocument {
    
    public HotlinkActionDocument(String info, List<HotlinkDescriptor> descriptors) {
        super(info, descriptors);
    }

    @Override
    public Object getContent() {
        return info;
    }

    @Override
    public boolean open() {
        return false;
    }

}
