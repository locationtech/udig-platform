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

import org.locationtech.udig.catalog.document.IHotlinkSource.HotlinkDescriptor;

/**
 * Document model for hotlink action documents.
 * 
 * @author Naz Chan
 */
public class ActionHotlinkDocument extends AbstractHotlinkDocument {
    
    public ActionHotlinkDocument(String info, List<HotlinkDescriptor> descriptors) {
        super(info, descriptors);
    }

    @Override
    public Object getContent() {
        return info;
    }
    
    @Override
    public String getContentName() {
        if (!isEmpty()) {
            return info;
        }
        return null;
    }

    @Override
    public boolean open() {
        return false;
    }

}
