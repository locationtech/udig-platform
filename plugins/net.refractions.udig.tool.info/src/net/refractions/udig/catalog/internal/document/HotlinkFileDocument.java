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

import java.io.File;
import java.util.List;

import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * Document model for hotlink file documents.
 * 
 * @author Naz Chan
 */
public class HotlinkFileDocument extends AbstractHotlinkDocument {

    protected File file;

    public HotlinkFileDocument(String info, List<HotlinkDescriptor> descriptors) {
        super(info, descriptors);
    }

    @Override
    public void setInfo(String info) {
        super.setInfo(info);
        file = AbstractDocument.createFile(info);
    }

    @Override
    public Object getValue() {
        return file;
    }

    @Override
    public boolean open() {
        return AbstractDocument.openFile(file);
    }

    @Override
    public boolean isEmpty() {
        return (file == null);
    }

}
