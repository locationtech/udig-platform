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

import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * Document model for hotlink web documents.
 * 
 * @author Naz Chan
 */
public class HotlinkWebDocument extends AbstractHotlinkDocument {

    protected URL url;

    public HotlinkWebDocument(String info, List<HotlinkDescriptor> descriptors) {
        super(info, descriptors);
    }
    
    @Override
    public void setInfo(String info) {
        super.setInfo(info);
        url = AbstractDocument.createUrl(info);
    }
    
    @Override
    public Object getContent() {
        return url;
    }

    @Override
    public String getContentName() {
        if (!isEmpty()) {
            return url.toString();
        }
        return null;
    }
    
    @Override
    public boolean open() {
        return AbstractDocument.openUrl(url);
    }

    @Override
    public boolean isEmpty() {
        return (url == null);
    }

}
