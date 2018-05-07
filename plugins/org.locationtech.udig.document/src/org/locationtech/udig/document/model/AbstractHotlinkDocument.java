/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.document.model;

import java.util.List;

import org.locationtech.udig.catalog.document.IHotlink;
import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

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
                final String description = descriptor.getDescription();
                if (description != null) {
                    sb.append(description);
                    if (count < descriptors.size()) {
                        sb.append(", "); //$NON-NLS-1$    
                    }    
                }
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public Type getType() {
        return Type.HOTLINK;
    }

    @Override
    public ContentType getContentType() {
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
