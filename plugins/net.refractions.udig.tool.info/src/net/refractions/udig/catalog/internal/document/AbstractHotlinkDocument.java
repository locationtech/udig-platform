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

import net.refractions.udig.catalog.document.IHotlink;
import net.refractions.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * Abstract model for hotlink documents.
 * 
 * @author Naz Chan
 */
public abstract class AbstractHotlinkDocument extends AbstractDocument implements IHotlink {

    protected String info;
    protected List<HotlinkDescriptor> descriptors;
    
    public AbstractHotlinkDocument(String info, List<HotlinkDescriptor> descriptors) {
        setInfo(info);
        this.descriptors = descriptors;
    }
    
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    @Override
    public List<HotlinkDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<HotlinkDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public String getLabel() {
        if (descriptors != null && descriptors.size() > 0) {
            int count = 0;
            final StringBuilder sb = new StringBuilder();
            for (HotlinkDescriptor descriptor : descriptors) {
                count++;
                sb.append(descriptor.getLabel());
                if (count < descriptors.size()) {
                    sb.append(", "); //$NON-NLS-1$    
                }
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public String getDescription() {
        if (descriptors != null && descriptors.size() > 0) {
            int count = 0;
            final StringBuilder sb = new StringBuilder();
            for (HotlinkDescriptor descriptor : descriptors) {
                count++;
                sb.append(descriptor.getDescription());
                if (count < descriptors.size()) {
                    sb.append(", "); //$NON-NLS-1$    
                }
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public DocType getDocType() {
        return DocType.HOTLINK;
    }

    @Override
    public Type getType() {
        if (descriptors != null && descriptors.size() > 0) {
            return descriptors.get(0).getType();
        }
        return null;
    }
    
    @Override
    public String getAttributeName() {
        if (descriptors != null && descriptors.size() > 0) {
            return descriptors.get(0).getAttributeName();
        }
        return null;
    }

    @Override
    public boolean isTemplate() {
        return false; // Hotlink documents cannot be templates
    }
    
}
